package me.hubertus248.deployer.application.service

import me.hubertus248.deployer.application.model.entity.Application
import me.hubertus248.deployer.application.model.entity.ApplicationName
import me.hubertus248.deployer.application.model.entity.Visibility
import me.hubertus248.deployer.common.exception.BadRequestException
import me.hubertus248.deployer.instancemanager.InstanceManagerName
import me.hubertus248.deployer.application.repository.ApplicationRepository
import me.hubertus248.deployer.common.exception.NotFoundException
import me.hubertus248.deployer.service.InstanceManagerService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.transaction.Transactional

interface ApplicationService {
    fun createApplication(name: ApplicationName, visibility: Visibility, managerName: InstanceManagerName): Application

    fun listApplications(pageable: Pageable): Page<Application>

    fun listPublicApplications(pageable: Pageable): Page<Application>

    fun findApplication(id: Long, includeRestricted: Boolean = false): Application?

    fun deleteApplication(appId: Long)
}

@Service
class ApplicationServiceImpl(
    private val applicationRepository: ApplicationRepository,
    private val instanceManagerService: InstanceManagerService
) : ApplicationService {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun createApplication(
        name: ApplicationName,
        visibility: Visibility,
        managerName: InstanceManagerName
    ): Application {
        val oldApp = applicationRepository.findFirstByName(name)
        if (oldApp != null) throw BadRequestException()

        val instanceManager = instanceManagerService.getManagerForName(managerName) ?: throw BadRequestException()
        val newApplication = instanceManager.registerApplication(name, visibility)
        logger.info("Created new application '${name.value}' of type '${instanceManager.getFriendlyName()}'")
        return newApplication
    }

    override fun listApplications(pageable: Pageable): Page<Application> {
        return applicationRepository.findAll(pageable)
    }

    override fun listPublicApplications(pageable: Pageable): Page<Application> {
        return applicationRepository.findAllPublic(pageable)
    }

    override fun findApplication(id: Long, includeRestricted: Boolean): Application? {
        return if (includeRestricted) applicationRepository.findFirstById(id)
        else applicationRepository.findFirstPublicById(id)
    }

    @Transactional
    override fun deleteApplication(appId: Long) {
        val application = applicationRepository.findFirstById(appId) ?: throw NotFoundException()
        val instanceManager = instanceManagerService.getManagerForApplication(application)
        instanceManager.prepareForDeletion(appId)

        applicationRepository.delete(application)
    }


}