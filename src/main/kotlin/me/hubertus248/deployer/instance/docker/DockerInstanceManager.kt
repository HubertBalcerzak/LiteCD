package me.hubertus248.deployer.instance.docker

import me.hubertus248.deployer.configuration.LitecdProperties
import me.hubertus248.deployer.data.dto.AvailableInstance
import me.hubertus248.deployer.data.entity.*
import me.hubertus248.deployer.exception.BadRequestException
import me.hubertus248.deployer.exception.NotFoundException
import me.hubertus248.deployer.instance.InstanceManager
import me.hubertus248.deployer.instance.InstanceManagerFeature
import me.hubertus248.deployer.instance.InstanceManagerName
import me.hubertus248.deployer.instance.docker.data.DockerApplication
import me.hubertus248.deployer.instance.docker.data.DockerInstance
import me.hubertus248.deployer.instance.docker.repository.DockerApplicationRepository
import me.hubertus248.deployer.instance.docker.repository.DockerInstanceRepository
import me.hubertus248.deployer.instance.docker.service.AvailableDockerInstanceService
import me.hubertus248.deployer.instance.docker.service.DockerConnectorService
import me.hubertus248.deployer.instance.spring.instance.SpringInstance
import me.hubertus248.deployer.service.EnvironmentService
import me.hubertus248.deployer.service.PortProviderService
import me.hubertus248.deployer.service.ZuulService
import me.hubertus248.deployer.util.Util
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.transaction.Transactional

val INSTANCE_MANAGER_DOCKER_NAME = InstanceManagerName("INSTANCE_MANAGER_CORE_DOCKER")


@Component
class DockerInstanceManager(
    private val dockerApplicationRepository: DockerApplicationRepository,
    private val dockerInstanceRepository: DockerInstanceRepository,
    private val litecdProperties: LitecdProperties,
    private val environmentService: EnvironmentService,
    private val dockerConnectorService: DockerConnectorService,
    private val availableDockerInstanceService: AvailableDockerInstanceService,
    private val portProviderService: PortProviderService,
    private val zuulService: ZuulService
) : InstanceManager() {
    override fun getFriendlyName(): String = "Docker Application"

    override fun getUniqueName(): InstanceManagerName = INSTANCE_MANAGER_DOCKER_NAME

    override fun registerApplication(name: ApplicationName, visibility: Visibility): Application {
        val dockerApplication =
            DockerApplication(null, null, null, Secret(Util.secureAlphanumericRandomString(32)), name, visibility)
        dockerApplicationRepository.save(dockerApplication)
        return dockerApplication
    }

    override fun listInstances(appId: Long, pageable: Pageable): List<Instance> {
        return dockerInstanceRepository.findAllByApplication_Id(appId, pageable)
            .onEach { updateInstanceStatus(it) }
    }

    override fun getAvailableFeatures(): Set<InstanceManagerFeature> = setOf(
        InstanceManagerFeature.POSSIBLE_INSTANCE_LIST,
        InstanceManagerFeature.CONFIGURABLE_APPLICATION,
        InstanceManagerFeature.CONFIGURABLE_INSTANCES,
        InstanceManagerFeature.STOPPABLE_INSTANCES
    )

    override fun getOpenUrl(instance: Instance): String =
        "${litecdProperties.protocol}://${(instance as SpringInstance).subdomain.value}.${litecdProperties.domain}"


    override fun prepareForApplicationDeletion(appId: Long) {
        val application = getApplication(appId)
        dockerInstanceRepository.findAllByApplication_Id(appId).forEach { deleteInstance(it, application) }
        environmentService.deleteDefaultEnvironment(application)
    }

    override fun getPossibleInstanceList(appId: Long, pageable: Pageable): List<AvailableInstance> =
        availableDockerInstanceService.listArtifacts(appId, pageable)
            .map { AvailableInstance(it.key, LocalDateTime.from(it.lastUpdate), it.actualInstance != null) }

    @Transactional
    override fun createInstance(appId: Long, instanceKey: InstanceKey): Instance {
        val application = getApplication(appId)
        val oldInstance = dockerInstanceRepository.findFirstByKeyAndApplication(instanceKey, application)
        if (oldInstance != null) throw BadRequestException()

        val instanceTemplate = availableDockerInstanceService.findArtifact(application, instanceKey)
            ?: throw BadRequestException()

        val newInstance = DockerInstance(
            DomainLabel.randomLabel(),
            null,
            mutableSetOf(),
            null,
            instanceKey,
            application,
        )
        environmentService.setDefaultInstanceEnvironment(newInstance)
        instanceTemplate.actualInstance = newInstance
        return newInstance
    }

    @Transactional
    override fun startInstance(instance: Instance) {
        instance as DockerInstance
        updateInstanceStatus(instance)
        if (instance.status == InstanceStatus.RUNNING) throw IllegalStateException("Instance already running")

        val port = portProviderService.getPort()
        instance.port = port

        dockerConnectorService.startContainer(instance)
        instance.zuulMappingId = zuulService.addMapping(
            "/api/spring/${instance.subdomain.value}/**",
            "http://localhost:${port.value}"
        )

        instance.status = InstanceStatus.RUNNING
        dockerInstanceRepository.save(instance)
    }

    @Transactional
    override fun stopInstance(appId: Long, instanceKey: InstanceKey) {
        stopInstance(getInstance(appId, instanceKey))
    }

    @Transactional
    override fun deleteInstance(appId: Long, instanceKey: InstanceKey) {
        deleteInstance(getInstance(appId, instanceKey), getApplication(appId))
    }

    @Transactional
    override fun recreateInstance(appId: Long, instanceKey: InstanceKey) {
        val instance = getInstance(appId, instanceKey)

        stopInstance(instance)
        startInstance(instance)
    }

    @Transactional
    override fun deleteAvailableInstance(appId: Long, instanceKey: InstanceKey) =
        availableDockerInstanceService.deleteArtifact(appId, instanceKey)

    private fun updateInstanceStatus(instance: DockerInstance) {
        instance.status = dockerConnectorService.getInstanceStatus(instance)
        dockerInstanceRepository.save(instance)
    }

    private fun deleteInstance(instance: DockerInstance, application: DockerApplication) {
        stopInstance(instance)

        val instanceTemplate = availableDockerInstanceService.findArtifact(application, instance.key)
            ?: throw IllegalStateException("Source template does not exist")

        dockerConnectorService.deleteImage(instance.key)
        environmentService.deleteInstanceEnvironment(instance)
        instanceTemplate.actualInstance = null
        dockerInstanceRepository.delete(instance)
    }

    private fun getInstance(appId: Long, instanceKey: InstanceKey): DockerInstance =
        dockerInstanceRepository.findFirstByKeyAndApplication(instanceKey, getApplication(appId))
            ?: throw NotFoundException()


    private fun getApplication(appId: Long) =
        dockerApplicationRepository.findFirstById(appId) ?: throw NotFoundException()

    private fun stopInstance(instance: DockerInstance) {
        instance.port?.let { portProviderService.freePort(it) }
        instance.zuulMappingId?.let { zuulService.removeMapping(it) }

        dockerConnectorService.stopContainer(instance)
        instance.apply {
            port = null
            zuulMappingId = null
            status = InstanceStatus.STOPPED
        }
        dockerInstanceRepository.save(instance)
    }

    override fun configureApplicationUrl(appId: Long): String = "/docker/configureApp/${appId}"

    override fun configureInstanceUrl(appId: Long, instanceId: Long): String = "/docker/configureInstance/$instanceId"
}