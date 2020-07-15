package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.data.dto.AvailableInstance
import me.hubertus248.deployer.data.entity.*
import me.hubertus248.deployer.exception.BadRequestException
import me.hubertus248.deployer.instance.InstanceManager
import me.hubertus248.deployer.instance.InstanceManagerFeature
import me.hubertus248.deployer.instance.InstanceManagerName
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import me.hubertus248.deployer.instance.spring.application.SpringApplicationRepository
import me.hubertus248.deployer.instance.spring.instance.AvailableSpringInstance
import me.hubertus248.deployer.instance.spring.instance.AvailableSpringInstanceService
import me.hubertus248.deployer.instance.spring.instance.SpringInstance
import me.hubertus248.deployer.instance.spring.instance.SpringInstanceRepository
import me.hubertus248.deployer.service.FilesystemStorageService
import me.hubertus248.deployer.service.WorkspaceService
import me.hubertus248.deployer.util.Util
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.lang.Exception
import java.lang.IllegalStateException
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
        private val filesystemStorageService: FilesystemStorageService
) : InstanceManager() {
    private val util = Util()

    override fun getFriendlyName(): String = "Spring Application"

    override fun getUniqueName(): InstanceManagerName = INSTANCE_MANAGER_SPRING_NAME

    override fun registerApplication(name: ApplicationName, visibility: Visibility): Application {
        val springApplication = SpringApplication(Secret(util.secureAlphanumericRandomString(32)), name, visibility)
        springApplicationRepository.save(springApplication)
        return springApplication
    }

    override fun listInstances(appId: Long, pageable: Pageable): List<Instance> {
        return springInstanceRepository.findAllByApplication_Id(appId, pageable)
    }

    override fun getAvailableFeatures(): Set<InstanceManagerFeature> = setOf(
            InstanceManagerFeature.CUSTOM_APPLICATION_INFO,
            InstanceManagerFeature.POSSIBLE_INSTANCE_LIST)

    override fun getOpenUrl(instance: Instance): String = "/open/spring/${instance.id}"

    override fun getCustomApplicationInfoFragment(): String = "application/spring/springApplicationInfo.html"

    override fun getPossibleInstanceList(appId: Long, pageable: Pageable): List<AvailableInstance> =
            availableSpringInstanceService.listArtifacts(appId, pageable).map { AvailableInstance(it.key, it.lastUpdate) }

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
            return SpringInstance(newWorkspace, instanceKey, application)
        } catch (e: Exception) {
            workspaceService.deleteWorkspace(newWorkspace)
            throw e
        }
    }

    private fun prepareWorkspace(workspace: Workspace, instanceTemplate: AvailableSpringInstance) {

        val workspaceRoot = workspaceService.getWorkspaceRoot(workspace)
        Files.copy(filesystemStorageService.getFileContent(instanceTemplate.artifact.fileKey)
                ?: throw IllegalStateException("Artifact not found"),
                Path.of(workspaceRoot.toString(), "app.jar"))
    }
}