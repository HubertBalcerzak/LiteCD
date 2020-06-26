package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.BadRequestException
import me.hubertus248.deployer.UnauthorizedException
import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.instance.InstanceManager
import me.hubertus248.deployer.instance.InstanceManagerFeature
import me.hubertus248.deployer.instance.InstanceManagerName
import me.hubertus248.deployer.service.FilesystemStorageService
import me.hubertus248.deployer.util.Util
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

interface StaticFileInstanceManager : InstanceManager {
    fun createInstance(appId: Long, secret: Secret, file: MultipartFile, instanceKey: InstanceKey)
}

@Component
class StaticFileInstanceManagerImpl(
        private val staticFileApplicationRepository: StaticFileApplicationRepository,
        private val filesystemStorageService: FilesystemStorageService,
        private val staticFileInstanceRepository: StaticFileInstanceRepository
) : StaticFileInstanceManager {

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
            val newInstance = StaticFileInstance(0, metadata, instanceKey, staticFileApplication)
            staticFileInstanceRepository.save(newInstance)
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


}