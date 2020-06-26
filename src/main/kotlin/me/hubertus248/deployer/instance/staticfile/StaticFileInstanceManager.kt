package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.BadRequestException
import me.hubertus248.deployer.UnauthorizedException
import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.instance.InstanceManager
import me.hubertus248.deployer.instance.InstanceManagerName
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

interface StaticFileInstanceManager : InstanceManager {
    fun createInstance(appId: Long, secret: Secret, file: MultipartFile, instanceKey: String)
}

@Component
class StaticFileInstanceManagerImpl(
        private val staticFileApplicationRepository: StaticFileApplicationRepository
) : StaticFileInstanceManager {

    override fun createInstance(appId: Long, secret: Secret, file: MultipartFile, instanceKey: String) {
        val staticFileApplication: StaticFileApplication = staticFileApplicationRepository.findFirstByApplication_Id(appId)
                ?: throw BadRequestException()

        if (secret != staticFileApplication.secret) throw UnauthorizedException()

        TODO("imeplement")
    }

    override fun getFriendlyName(): String = "Static file manager"

    override fun getUniqueName(): InstanceManagerName = InstanceManagerName("INSTANCE_MANAGER_CORE_STATIC_FILE")

    override fun registerApplication(application: Application) {
        TODO("Not yet implemented")
    }


}