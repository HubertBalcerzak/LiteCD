package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.ApplicationName
import me.hubertus248.deployer.data.entity.Visibility
import me.hubertus248.deployer.exception.BadRequestException
import me.hubertus248.deployer.instance.InstanceManagerName
import me.hubertus248.deployer.data.reposiotry.ApplicationRepository
import me.hubertus248.deployer.exception.NotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import javax.transaction.Transactional

interface ApplicationService {
    fun createApplication(name: ApplicationName, visibility: Visibility, managerName: InstanceManagerName): Long

    fun listApplications(pageable: Pageable): Set<Application>

    fun listPublicApplications(pageable: Pageable): Set<Application>

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
    override fun createApplication(name: ApplicationName, visibility: Visibility, managerName: InstanceManagerName): Long {
        //TODO check name not unique
        val instanceManager = instanceManagerService.getManagerForName(managerName) ?: throw BadRequestException()
        val newApplication = instanceManager.registerApplication(name, visibility)
        logger.info("Created new application '${name.value}' of type '${instanceManager.getFriendlyName()}'")
        return newApplication.id
    }

    override fun listApplications(pageable: Pageable): Set<Application> {
        return applicationRepository.findAll(pageable).toSet()
    }

    override fun listPublicApplications(pageable: Pageable): Set<Application> {
        return applicationRepository.findAllPublic(pageable).toSet()
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