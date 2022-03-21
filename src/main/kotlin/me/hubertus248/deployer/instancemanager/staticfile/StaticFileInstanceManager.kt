package me.hubertus248.deployer.instancemanager.staticfile

import me.hubertus248.deployer.applications.model.entity.Application
import me.hubertus248.deployer.applications.model.entity.ApplicationName
import me.hubertus248.deployer.applications.model.entity.Visibility
import me.hubertus248.deployer.common.exception.AccessDeniedException
import me.hubertus248.deployer.common.exception.BadRequestException
import me.hubertus248.deployer.common.exception.NotFoundException
import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.data.entity.Secret
import me.hubertus248.deployer.instancemanager.InstanceManager
import me.hubertus248.deployer.instancemanager.InstanceManagerFeature
import me.hubertus248.deployer.instancemanager.InstanceManagerName
import me.hubertus248.deployer.service.FilesystemStorageService
import me.hubertus248.deployer.util.Util
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.http.ContentDisposition
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.nio.charset.Charset
import javax.servlet.http.HttpServletResponse

val INSTANCE_MANAGER_STATIC_FILE_NAME = InstanceManagerName("INSTANCE_MANAGER_CORE_STATIC_FILE")

interface StaticFileInstanceManager {
    fun createInstance(appId: Long, secret: Secret, file: MultipartFile, instanceKey: InstanceKey)

    fun openInstance(instanceId: Long, servletResponse: HttpServletResponse)
}

@Component
class StaticFileInstanceManagerImpl(
        private val staticFileApplicationRepository: StaticFileApplicationRepository,
        private val filesystemStorageService: FilesystemStorageService,
        private val staticFileInstanceRepository: StaticFileInstanceRepository
) : InstanceManager(), StaticFileInstanceManager {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun createInstance(appId: Long, secret: Secret, file: MultipartFile, instanceKey: InstanceKey) {
        val staticFileApplication: StaticFileApplication = staticFileApplicationRepository.findFirstById(appId)
                ?: throw BadRequestException()

        if (secret != staticFileApplication.secret) throw AccessDeniedException()

        val instance = staticFileInstanceRepository.findFirstByKeyAndApplication(instanceKey,
                staticFileApplication)

        val metadata = filesystemStorageService.saveFile(file.inputStream,
                file.originalFilename ?: "file",
                file.contentType ?: "text/plain;charset=utf-8")

        if (instance != null) {
            val oldFileKey = instance.fileMetadata.fileKey
            instance.fileMetadata = metadata
            staticFileInstanceRepository.save(instance)
            filesystemStorageService.deleteFile(oldFileKey)
        } else {
            val newInstance = StaticFileInstance(metadata, staticFileApplication, instanceKey)
            staticFileInstanceRepository.save(newInstance)
        }

        logger.info("Created new Static File instance with key ${instanceKey.value}, for app ${staticFileApplication.name.value}")

    }

    override fun openInstance(instanceId: Long, servletResponse: HttpServletResponse) {
        val instance = staticFileInstanceRepository.findFirstById(instanceId) ?: throw NotFoundException()
        servletResponse.contentType = instance.fileMetadata.contentType

        servletResponse.addHeader("Content-Disposition",
                ContentDisposition
                        .builder("inline")
                        .filename(instance.fileMetadata.filename, Charset.forName("UTF-8"))
                        .build()
                        .toString())

        val fileStream = filesystemStorageService.getFileContent(instance.fileMetadata.fileKey)
                ?: throw IllegalStateException("File does not exist")
        try {
            IOUtils.copyLarge(fileStream, servletResponse.outputStream)
            servletResponse.outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fileStream.close()
        }

    }

    override fun getFriendlyName(): String = "Static File Application"

    override fun getUniqueName(): InstanceManagerName = INSTANCE_MANAGER_STATIC_FILE_NAME

    override fun registerApplication(name: ApplicationName, visibility: Visibility): Application {
        val staticFileApplication = StaticFileApplication(Secret(Util.secureAlphanumericRandomString(32)), name, visibility)
        staticFileApplicationRepository.save(staticFileApplication)
        return staticFileApplication
    }

    override fun listInstances(appId: Long, pageable: Pageable): List<Instance> {
        return staticFileInstanceRepository.findAllByApplication_Id(appId, pageable)
    }

    override fun getAvailableFeatures(): Set<InstanceManagerFeature> = setOf(InstanceManagerFeature.CUSTOM_APPLICATION_INFO)

    override fun getOpenUrl(instance: Instance): String = "/open/staticfile/${instance.id}"

    override fun prepareForDeletion(appId: Long) {
        staticFileInstanceRepository.findAllByApplication_Id(appId).forEach { deleteInstance(it) }
    }

    override fun getCustomApplicationInfoFragment(): String = "application/staticfile/staticFileApplicationInfo.html"

    override fun deleteInstance(appId: Long, instanceKey: InstanceKey) {
        val application = staticFileApplicationRepository.findFirstById(appId) ?: throw NotFoundException()
        val instance = staticFileInstanceRepository.findFirstByKeyAndApplication(instanceKey, application)
                ?: throw NotFoundException()

        deleteInstance(instance)
    }

    private fun deleteInstance(instance: StaticFileInstance) {
        filesystemStorageService.deleteFile(instance.fileMetadata.fileKey)
        staticFileInstanceRepository.delete(instance)
    }
}