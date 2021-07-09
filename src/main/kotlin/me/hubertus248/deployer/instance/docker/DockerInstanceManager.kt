package me.hubertus248.deployer.instance.docker

import me.hubertus248.deployer.configuration.LitecdProperties
import me.hubertus248.deployer.data.entity.*
import me.hubertus248.deployer.exception.NotFoundException
import me.hubertus248.deployer.instance.InstanceManager
import me.hubertus248.deployer.instance.InstanceManagerFeature
import me.hubertus248.deployer.instance.InstanceManagerName
import me.hubertus248.deployer.instance.docker.data.DockerApplication
import me.hubertus248.deployer.instance.docker.data.DockerInstance
import me.hubertus248.deployer.instance.docker.repository.DockerApplicationRepository
import me.hubertus248.deployer.instance.docker.repository.DockerInstanceRepository
import me.hubertus248.deployer.instance.spring.instance.SpringInstance
import me.hubertus248.deployer.service.EnvironmentService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

val INSTANCE_MANAGER_DOCKER_NAME = InstanceManagerName("INSTANCE_MANAGER_CORE_DOCKER")


@Component
class DockerInstanceManager(
    private val dockerApplicationRepository: DockerApplicationRepository,
    private val dockerInstanceRepository: DockerInstanceRepository,
    private val litecdProperties: LitecdProperties,
    private val environmentService: EnvironmentService
) : InstanceManager() {
    override fun getFriendlyName(): String = "Docker Application"

    override fun getUniqueName(): InstanceManagerName = INSTANCE_MANAGER_DOCKER_NAME

    override fun registerApplication(name: ApplicationName, visibility: Visibility): Application {
        val dockerApplication = DockerApplication(null, null, null, name, visibility)
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


    override fun prepareForDeletion(appId: Long) {
        val application = dockerApplicationRepository.findFirstById(appId) ?: throw NotFoundException()
        dockerInstanceRepository.findAllByApplication_Id(appId).forEach { deleteInstance(it, application) }
        environmentService.deleteDefaultEnvironment(application)
    }

    override fun createInstance(appId: Long, instanceKey: InstanceKey): Instance {
        return super.createInstance(appId, instanceKey)
    }

    override fun startInstance(instance: Instance) {
        super.startInstance(instance)
    }

    override fun stopInstance(appId: Long, instanceKey: InstanceKey) {
        super.stopInstance(appId, instanceKey)
    }

    override fun deleteInstance(appId: Long, instanceKey: InstanceKey) {
        super.deleteInstance(appId, instanceKey)
    }

    override fun recreateInstance(appId: Long, instanceKey: InstanceKey) {
        super.recreateInstance(appId, instanceKey)
    }

    private fun updateInstanceStatus(instance: DockerInstance) {

    }

    private fun deleteInstance(instance: DockerInstance, application: DockerApplication) {

    }

    override fun configureApplicationUrl(appId: Long): String = "/docker/configureApp/${appId}"

    override fun configureInstanceUrl(appId: Long, instanceId: Long): String = "/docker/configureInstance/$instanceId"
}