package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.instance.InstanceManager
import me.hubertus248.deployer.instance.InstanceManagerName
import org.springframework.stereotype.Service

interface InstanceManagerService {
    fun getAvailableManagers(): List<InstanceManager>
    fun verifyManagerExists(managerName: InstanceManagerName): Boolean
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

}
