package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.exception.BadRequestException
import me.hubertus248.deployer.exception.UnauthorizedException
import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.exception.NotFoundException
import me.hubertus248.deployer.instance.InstanceManager
import me.hubertus248.deployer.instance.InstanceManagerFeature
import me.hubertus248.deployer.instance.InstanceManagerName
import me.hubertus248.deployer.service.FilesystemStorageService
import me.hubertus248.deployer.util.Util
import org.apache.commons.io.IOUtils
import org.springframework.data.domain.Pageable
import org.springframework.http.ContentDisposition
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.lang.IllegalStateException
import java.nio.charset.Charset
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

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

    private val util: Util = Util()

    override fun createInstance(appId: Long, secret: Secret, file: MultipartFile, instanceKey: InstanceKey) {
        val staticFileApplication: StaticFileApplication = staticFileApplicationRepository.findFirstByApplication_Id(appId)
                ?: throw BadRequestException()

        if (secret != staticFileApplication.secret) throw UnauthorizedException()

        val instance = staticFileInstanceRepository.findFirstByKeyAndStaticFileApplication(instanceKey,
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

    override fun getFriendlyName(): String = "Static file manager"

    override fun getUniqueName(): InstanceManagerName = InstanceManagerName("INSTANCE_MANAGER_CORE_STATIC_FILE")

    override fun registerApplication(application: Application) {
        val staticFileApplication = StaticFileApplication(0, application, Secret(util.secureAlphanumericRandomString(32)))
        staticFileApplicationRepository.save(staticFileApplication)
    }

    override fun listInstances(appId: Long, pageable: Pageable): List<Instance> {
        return staticFileInstanceRepository.findAllByApplicationId(appId, pageable)
    }

    override fun getAvailableFeatures(): Set<InstanceManagerFeature> = setOf(InstanceManagerFeature.CUSTOM_APPLICATION_INFO)

    override fun getOpenUrl(instance: Instance): String = "/open/staticfile/${instance.id}"


}