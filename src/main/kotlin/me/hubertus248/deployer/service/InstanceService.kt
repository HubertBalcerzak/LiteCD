package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.entity.InstanceKey
import org.springframework.stereotype.Service

interface InstanceService {
    fun startInstance(appId: Long, instanceKey: InstanceKey)
}

@Service
class InstanceServiceImpl() : InstanceService {
    override fun startInstance(appId: Long, instanceKey: InstanceKey) {
        TODO("Not yet implemented")
    }

}