package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.exception.BadRequestException
import org.springframework.stereotype.Service

interface InstanceService {
    fun createAndStart(appId: Long, instanceKey: InstanceKey)
}

@Service
class InstanceServiceImpl(
        private val applicationService: ApplicationService,
        private val instanceManagerService: InstanceManagerService
) : InstanceService {
    override fun createAndStart(appId: Long, instanceKey: InstanceKey) {
        val application = applicationService.findApplication(appId, true) ?: throw BadRequestException()

        val instanceManager = instanceManagerService.getManagerForApplication(application)

        val newInstance = instanceManager.createInstance(appId, instanceKey)
        instanceManager.startInstance(newInstance)
    }

}