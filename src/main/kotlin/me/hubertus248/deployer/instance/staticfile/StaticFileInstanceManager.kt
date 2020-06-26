package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.instance.InstanceManager
import me.hubertus248.deployer.instance.InstanceManagerName
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile

interface StaticFileInstanceManager : InstanceManager {
    fun createInstance(appId: Long, secret: String, file: MultipartFile, instanceKey: String)
}

@Component
class StaticFileInstanceManagerImpl(
) : StaticFileInstanceManager {

    override fun createInstance(appId: Long, secret: String, file: MultipartFile, instanceKey: String) {

    }

    override fun getFriendlyName(): String = "Static file manager"

    override fun getUniqueName(): InstanceManagerName = InstanceManagerName("INSTANCE_MANAGER_CORE_STATIC_FILE")

    override fun registerApplication(application: Application) {
        TODO("Not yet implemented")
    }


}