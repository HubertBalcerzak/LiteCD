package me.hubertus248.deployer.instance.docker.service

import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.data.entity.Secret
import me.hubertus248.deployer.exception.BadRequestException
import me.hubertus248.deployer.exception.UnauthorizedException
import me.hubertus248.deployer.instance.docker.data.AvailableDockerInstance
import me.hubertus248.deployer.instance.docker.data.DockerApplication
import me.hubertus248.deployer.instance.docker.repository.AvailableDockerInstanceRepository
import me.hubertus248.deployer.instance.docker.repository.DockerApplicationRepository
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import me.hubertus248.deployer.instance.spring.instance.AvailableSpringInstance
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.lang.IllegalStateException
import java.time.Instant
import java.time.LocalDateTime
import javax.transaction.Transactional

interface AvailableDockerInstanceService {

    fun registerInstance(app: Long, secret: Secret, instanceKey: InstanceKey)

    fun deleteArtifact(appId: Long, instanceKey: InstanceKey)

    fun deleteAllArtifacts(application: DockerApplication)

    fun listArtifacts(appId: Long, pageable: Pageable): List<AvailableDockerInstance>

    fun findArtifact(
        dockerApplication: DockerApplication,
        instanceKey: InstanceKey
    ): AvailableDockerInstance?
}

@Service
class AvailableDockerInstanceServiceImpl(
    private val availableDockerInstanceRepository: AvailableDockerInstanceRepository,
    private val dockerApplicationRepository: DockerApplicationRepository
) : AvailableDockerInstanceService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun registerInstance(appId: Long, secret: Secret, instanceKey: InstanceKey) {
        val app = dockerApplicationRepository.findFirstById(appId) ?: throw BadRequestException()

        if (secret != app.secret) throw UnauthorizedException()

        val older = availableDockerInstanceRepository.findFirstByApplicationAndKey(app, instanceKey)

        if (older != null) {
            older.lastUpdate = Instant.now()
            availableDockerInstanceRepository.save(older)
        } else {
            AvailableDockerInstance(0, instanceKey, app, Instant.now(), Instant.now())
                .let { availableDockerInstanceRepository.save(it) }

        }
        logger.info("Added available instance with key '${instanceKey.value}' for app '${app.name.value}'")
    }

    @Transactional
    override fun deleteArtifact(appId: Long, instanceKey: InstanceKey) {
        val availableInstance = availableDockerInstanceRepository.findFirstByApplication_IdAndKey(appId, instanceKey)
            ?: throw BadRequestException()
        if (availableInstance.actualInstance != null) throw IllegalStateException("Cannot delete instance template: Delete created instance first.")

        deleteArtifact(availableInstance)

    }

    override fun deleteAllArtifacts(application: DockerApplication) {
        availableDockerInstanceRepository.findAllByApplication(application).forEach { deleteArtifact(it) }
    }

    override fun listArtifacts(appId: Long, pageable: Pageable): List<AvailableDockerInstance> {
        return availableDockerInstanceRepository.findAllByApplication_Id(appId, pageable)
    }

    override fun findArtifact(
        dockerApplication: DockerApplication,
        instanceKey: InstanceKey
    ): AvailableDockerInstance? {
        return availableDockerInstanceRepository.findFirstByApplicationAndKey(dockerApplication, instanceKey)
    }

    private fun deleteArtifact(availableInstance: AvailableDockerInstance) {
        availableDockerInstanceRepository.delete(availableInstance)
    }
}