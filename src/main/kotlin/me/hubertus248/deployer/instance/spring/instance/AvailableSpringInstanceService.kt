package me.hubertus248.deployer.instance.spring.instance

import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.data.entity.Secret
import me.hubertus248.deployer.exception.BadRequestException
import me.hubertus248.deployer.exception.UnauthorizedException
import me.hubertus248.deployer.instance.spring.application.SpringApplicationRepository
import me.hubertus248.deployer.service.FilesystemStorageService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import javax.transaction.Transactional

interface AvailableSpringInstanceService {
    fun addArtifact(appId: Long, secret: Secret, file: MultipartFile, instanceKey: InstanceKey)

    fun deleteArtifact(appId: Long, instanceKey: InstanceKey)

    fun listArtifacts(appId: Long, pageable: Pageable): List<AvailableSpringInstance>
}

@Service
class AvailableSpringInstanceServiceImpl(
        private val availableSpringInstanceRepository: AvailableSpringInstanceRepository,
        private val springApplicationRepository: SpringApplicationRepository,
        private val filesystemStorageService: FilesystemStorageService
) : AvailableSpringInstanceService {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun addArtifact(appId: Long, secret: Secret, file: MultipartFile, instanceKey: InstanceKey) {
        val app = springApplicationRepository.findFirstById(appId) ?: throw BadRequestException()

        if (secret != app.secret) throw UnauthorizedException()

        val older = availableSpringInstanceRepository.findFirstByApplicationAndKey(app, instanceKey)

        val savedFileMeta = filesystemStorageService.saveFile(file.inputStream, "app.jar", "application/octet-stream")
        if (older != null) {
            val oldFileKey = older.artifact.fileKey
            older.artifact = savedFileMeta
            older.lastUpdate = LocalDateTime.now()
            availableSpringInstanceRepository.save(older)
            filesystemStorageService.deleteFile(oldFileKey)
        } else {
            val newAvailableSpringInstance = AvailableSpringInstance(0, savedFileMeta, instanceKey, app, LocalDateTime.now(), LocalDateTime.now())
            availableSpringInstanceRepository.save(newAvailableSpringInstance)
        }
        logger.info("Added available instance with key '${instanceKey.value}' for app '${app.name.value}'")
    }

    @Transactional
    override fun deleteArtifact(appId: Long, instanceKey: InstanceKey) {
        val availableInstance = availableSpringInstanceRepository.findFirstByApplication_IdAndKey(appId, instanceKey)
                ?: throw BadRequestException()

        val fileKey = availableInstance.artifact.fileKey
        availableInstance.deleted = true
        availableSpringInstanceRepository.save(availableInstance)
        filesystemStorageService.deleteFile(fileKey)

    }

    override fun listArtifacts(appId: Long, pageable: Pageable): List<AvailableSpringInstance> {
        return availableSpringInstanceRepository.findAllByApplication_Id(appId, pageable)
    }

}