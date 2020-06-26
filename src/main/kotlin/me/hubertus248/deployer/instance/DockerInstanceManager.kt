package me.hubertus248.deployer.instance

import me.hubertus248.deployer.data.entity.Application
import org.springframework.stereotype.Component

@Component
class DockerInstanceManager() : InstanceManager {

    override fun getFriendlyName(): String = "Docker manager"

    override fun getUniqueName(): InstanceManagerName = InstanceManagerName("INSTANCE_MANAGER_CORE_DOCKER")
    override fun registerApplication(application: Application) {
        TODO("Not yet implemented")
    }
}