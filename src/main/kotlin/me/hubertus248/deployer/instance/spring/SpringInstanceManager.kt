package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.data.dto.AvailableInstance
import me.hubertus248.deployer.data.entity.*
import me.hubertus248.deployer.exception.BadRequestException
import me.hubertus248.deployer.exception.NotFoundException
import me.hubertus248.deployer.instance.InstanceManager
import me.hubertus248.deployer.instance.InstanceManagerFeature
import me.hubertus248.deployer.instance.InstanceManagerName
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import me.hubertus248.deployer.instance.spring.application.SpringApplicationRepository
import me.hubertus248.deployer.instance.spring.instance.AvailableSpringInstance
import me.hubertus248.deployer.instance.spring.instance.AvailableSpringInstanceService
import me.hubertus248.deployer.instance.spring.instance.SpringInstance
import me.hubertus248.deployer.instance.spring.instance.SpringInstanceRepository
import me.hubertus248.deployer.service.*
import me.hubertus248.deployer.util.Util
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import javax.transaction.Transactional

val INSTANCE_MANAGER_SPRING_NAME = InstanceManagerName("INSTANCE_MANAGER_CORE_SPRING")


@Component
class SpringInstanceManager(
        private val springApplicationRepository: SpringApplicationRepository,
        private val springInstanceRepository: SpringInstanceRepository,
        private val availableSpringInstanceService: AvailableSpringInstanceService,
        private val workspaceService: WorkspaceService,
        private val filesystemStorageService: FilesystemStorageService,
        private val subProcessService: SubProcessService,
        private val zuulService: ZuulService,
        private val springEnvironmentService: SpringEnvironmentService,
        private val portProviderService: PortProviderService
) : InstanceManager() {

    @Value("\${deployer.domain}")
    private val domain: String = ""

    @Value("\${deployer.protocol}")
    private val protocol: String = ""

    override fun getFriendlyName(): String = "Spring Application"

    override fun getUniqueName(): InstanceManagerName = INSTANCE_MANAGER_SPRING_NAME

    override fun registerApplication(name: ApplicationName, visibility: Visibility): Application {
        val springApplication = SpringApplication(Secret(Util.secureAlphanumericRandomString(32)), name, visibility)
        springApplicationRepository.save(springApplication)
        return springApplication
    }

    override fun listInstances(appId: Long, pageable: Pageable): List<Instance> {
        return springInstanceRepository.findAllByApplication_Id(appId, pageable).apply { this.forEach { updateInstanceStatus(it) } }
    }

    override fun getAvailableFeatures(): Set<InstanceManagerFeature> = setOf(
            InstanceManagerFeature.CUSTOM_APPLICATION_INFO,
            InstanceManagerFeature.POSSIBLE_INSTANCE_LIST,
            InstanceManagerFeature.CONFIGURABLE_APPLICATION,
            InstanceManagerFeature.CONFIGURABLE_INSTANCES,
            InstanceManagerFeature.STOPPABLE_INSTANCES)

    override fun getOpenUrl(instance: Instance): String = "$protocol://${(instance as SpringInstance).subdomain.value}.$domain"

    override fun getCustomApplicationInfoFragment(): String = "application/spring/springApplicationInfo.html"

    override fun getPossibleInstanceList(appId: Long, pageable: Pageable): List<AvailableInstance> =
            availableSpringInstanceService.listArtifacts(appId, pageable)
                    .map { AvailableInstance(it.key, it.lastUpdate, it.actualInstance != null) }

    @Transactional
    override fun createInstance(appId: Long, instanceKey: InstanceKey): Instance {
        val application = springApplicationRepository.findFirstById(appId) ?: throw BadRequestException()
        val oldInstance = springInstanceRepository.findFirstByKeyAndApplication(instanceKey, application)
        if (oldInstance != null) throw BadRequestException()

        val instanceTemplate = availableSpringInstanceService.findArtifact(application, instanceKey)
                ?: throw BadRequestException()

        val newWorkspace = workspaceService.createWorkspace()

        try {
            prepareWorkspace(newWorkspace, instanceTemplate)
            val newInstance = SpringInstance(newWorkspace,
                    null,
                    DomainLabel.randomLabel(),
                    null,
                    mutableSetOf(),
                    null,
                    instanceKey,
                    application)
            springEnvironmentService.setDefaultInstanceEnvironment(newInstance)
            instanceTemplate.actualInstance = newInstance
            return newInstance
        } catch (e: Exception) {
            workspaceService.deleteWorkspace(newWorkspace)
            throw e
        }
    }

    @Transactional
    override fun startInstance(instance: Instance) {
        val springInstance = instance as SpringInstance
        updateInstanceStatus(springInstance)
        if (springInstance.status == InstanceStatus.RUNNING) throw IllegalStateException("Instance already running")

        val workspaceRootPath = workspaceService.getWorkspaceRoot(springInstance.workspace)
        springInstance.port = portProviderService.getPort()

        springInstance.process = subProcessService.startProcess(
                workspaceRootPath.toFile(),
                Path.of(workspaceRootPath.toString(), "log.txt").toFile(),
                listOf("java", "-jar", "app.jar"),//TODO allow customization
                springEnvironmentService.getEnvironment(springInstance)
        )
        springInstance.zuulMappingId = zuulService.addMapping(
                "/api/spring/${springInstance.subdomain.value}/**",
                "http://localhost:${instance.port}"
        )

        springInstance.status = InstanceStatus.RUNNING
        springInstanceRepository.save(springInstance)
    }

    private fun prepareWorkspace(workspace: Workspace, instanceTemplate: AvailableSpringInstance) {

        val workspaceRoot = workspaceService.getWorkspaceRoot(workspace)
        Files.copy(filesystemStorageService.getFileContent(instanceTemplate.artifact.fileKey)
                ?: throw IllegalStateException("Artifact not found"),
                Path.of(workspaceRoot.toString(), "app.jar"))
    }

    private fun updateInstanceStatus(instance: SpringInstance) {
        if (instance.status == InstanceStatus.STOPPED) return
        val process = instance.process
        if (process == null || subProcessService.getProcessStatus(process) == SubProcessStatus.DEAD) {
            instance.process = null
            instance.status = InstanceStatus.STOPPED
            springInstanceRepository.save(instance)
        }
    }

    @Transactional
    override fun stopInstance(appId: Long, instanceKey: InstanceKey) {
        val application = springApplicationRepository.findFirstById(appId) ?: throw NotFoundException()
        val instance = springInstanceRepository.findFirstByKeyAndApplication(instanceKey, application)
                ?: throw NotFoundException()

        stopInstance(instance)
    }

    @Transactional
    override fun deleteInstance(appId: Long, instanceKey: InstanceKey) {
        val application = springApplicationRepository.findFirstById(appId) ?: throw NotFoundException()
        val instance = springInstanceRepository.findFirstByKeyAndApplication(instanceKey, application)
                ?: throw NotFoundException()

        deleteInstance(instance, application)

    }

    @Transactional
    override fun recreateInstance(appId: Long, instanceKey: InstanceKey) {
        val application = springApplicationRepository.findFirstById(appId) ?: throw NotFoundException()
        val instance = springInstanceRepository.findFirstByKeyAndApplication(instanceKey, application)
                ?: throw NotFoundException()

        stopInstance(instance)

        val instanceTemplate = availableSpringInstanceService.findArtifact(application, instanceKey)
                ?: throw IllegalStateException("Instance template for application ${application.name.value} and key ${instance.key.value} does not exist")

        workspaceService.clearWorkspace(instance.workspace)
        prepareWorkspace(instance.workspace, instanceTemplate)
        startInstance(instance)

    }

    private fun stopInstance(instance: SpringInstance) {
        instance.process?.let { subProcessService.stopProcess(it) }
        instance.port?.let { portProviderService.freePort(it) }
        instance.zuulMappingId?.let { zuulService.removeMapping(it) }

        instance.apply {
            process = null
            port = null
            zuulMappingId = null
            status = InstanceStatus.STOPPED
        }
        springInstanceRepository.save(instance)
    }

    private fun deleteInstance(instance: SpringInstance, application: SpringApplication) {
        stopInstance(instance)

        val instanceTemplate = availableSpringInstanceService.findArtifact(application, instance.key)
                ?: throw IllegalStateException("Source template does not exist")

        workspaceService.deleteWorkspace(instance.workspace)
        springEnvironmentService.deleteInstanceEnvironment(instance)
        instanceTemplate.actualInstance = null
        springInstanceRepository.delete(instance)
    }

    override fun deleteAvailableInstance(appId: Long, instanceKey: InstanceKey) {
         availableSpringInstanceService.deleteArtifact(appId, instanceKey)

    }

    override fun configureApplicationUrl(appId: Long): String = "/spring/configureApp/${appId}"

    override fun configureInstanceUrl(appId: Long, instanceId: Long): String = "/spring/configureInstance/$instanceId"
}