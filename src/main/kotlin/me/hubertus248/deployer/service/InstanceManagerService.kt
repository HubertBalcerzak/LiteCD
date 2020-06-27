package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.exception.ApplicationCorruptedException
import me.hubertus248.deployer.instance.InstanceManager
import me.hubertus248.deployer.instance.InstanceManagerName
import org.springframework.stereotype.Service

interface InstanceManagerService {
    fun getAvailableManagers(): List<InstanceManager>
    fun verifyManagerExists(managerName: InstanceManagerName): Boolean
    fun getManagerForApplication(application: Application): InstanceManager
    fun getManagerForName(managerName: InstanceManagerName):InstanceManager?
}

@ExperimentalStdlibApi
@Service
class InstanceManagerServiceImpl(
        private val instanceManagers: List<InstanceManager>
) : InstanceManagerService {

    private val managerMap: Map<InstanceManagerName, InstanceManager> = buildMap {
        instanceManagers.forEach { put(it.getUniqueName(), it) }
    }


    override fun getAvailableManagers(): List<InstanceManager> = instanceManagers
    override fun verifyManagerExists(managerName: InstanceManagerName): Boolean {
        return managerMap.containsKey(managerName)
    }

    override fun getManagerForApplication(application: Application): InstanceManager {
        return managerMap[application.manager]
                ?: throw ApplicationCorruptedException("Instance manager ${application.manager} not found for application ${application.id}")
    }

    override fun getManagerForName(managerName: InstanceManagerName): InstanceManager? {
        return managerMap[managerName]
    }

}
