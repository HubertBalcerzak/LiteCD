package me.hubertus248.deployer.service

import me.hubertus248.deployer.application.service.ApplicationService
import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.data.reposiotry.InstanceRepository
import me.hubertus248.deployer.common.exception.BadRequestException
import me.hubertus248.deployer.common.exception.NotFoundException
import me.hubertus248.deployer.instancemanager.InstanceManager
import me.hubertus248.deployer.instancemanager.InstanceManagerFeature
import org.springframework.stereotype.Service

interface InstanceService {
    fun createAndStart(appId: Long, instanceKey: InstanceKey)

    fun start(appId: Long, instanceKey: InstanceKey)

    fun stop(appId: Long, instanceKey: InstanceKey)

    fun delete(appId: Long, instanceKey: InstanceKey)

    fun recreate(appId: Long, instanceKey: InstanceKey)

    fun deleteAvailableInstance(appId: Long, instanceKey: InstanceKey)
}

@Service
class InstanceServiceImpl(
    private val applicationService: ApplicationService,
    private val instanceManagerService: InstanceManagerService,
    private val instanceRepository: InstanceRepository<Instance>
) : InstanceService {
    override fun createAndStart(appId: Long, instanceKey: InstanceKey) {
        val instanceManager = getInstanceManager(appId)

        if (!instanceManager.supportsFeature(InstanceManagerFeature.POSSIBLE_INSTANCE_LIST))
            throw BadRequestException()

        val newInstance = instanceManager.createInstance(appId, instanceKey)
        instanceManager.startInstance(newInstance)
    }

    override fun start(appId: Long, instanceKey: InstanceKey) {
        val application = applicationService.findApplication(appId, true) ?: throw BadRequestException()

        val instanceManager = instanceManagerService.getManagerForApplication(application)
        if (!instanceManager.supportsFeature(InstanceManagerFeature.STOPPABLE_INSTANCES))
            throw BadRequestException()

        val instance = instanceRepository.findFirstByKeyAndApplication(instanceKey, application)
                ?: throw NotFoundException()

        instanceManager.startInstance(instance)

    }

    override fun stop(appId: Long, instanceKey: InstanceKey) {

        val instanceManager = getInstanceManager(appId)
        if (!instanceManager.supportsFeature(InstanceManagerFeature.STOPPABLE_INSTANCES))
            throw BadRequestException()

        instanceManager.stopInstance(appId, instanceKey)

    }

    override fun delete(appId: Long, instanceKey: InstanceKey) {
        val instanceManager = getInstanceManager(appId)

        instanceManager.deleteInstance(appId, instanceKey)
    }

    override fun recreate(appId: Long, instanceKey: InstanceKey) {
        val instanceManager = getInstanceManager(appId)

        if (!instanceManager.supportsFeature(InstanceManagerFeature.POSSIBLE_INSTANCE_LIST))
            throw BadRequestException()

        instanceManager.recreateInstance(appId, instanceKey)
    }

    override fun deleteAvailableInstance(appId: Long, instanceKey: InstanceKey) {
        val instanceManager = getInstanceManager(appId)

        if (!instanceManager.supportsFeature(InstanceManagerFeature.POSSIBLE_INSTANCE_LIST))
            throw BadRequestException()

        instanceManager.deleteAvailableInstance(appId, instanceKey)
    }


    private fun getInstanceManager(appId: Long): InstanceManager {
        val application = applicationService.findApplication(appId, true)
                ?: throw BadRequestException()
        return instanceManagerService.getManagerForApplication(application)
    }
}