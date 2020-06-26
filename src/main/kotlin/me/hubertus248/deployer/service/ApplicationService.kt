package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.Visibility
import me.hubertus248.deployer.instance.InstanceManagerName
import me.hubertus248.deployer.reposiotry.ApplicationRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

interface ApplicationService {
    fun createApplication(name: String, visibility: Visibility, managerName: InstanceManagerName): Long

    fun listApplications(pageable: Pageable): Set<Application>

    fun listPublicApplications(pageable: Pageable): Set<Application>

    fun findApplication(id: Long, includeRestricted: Boolean = false): Application?
}

@Service
class ApplicationServiceImpl(
        private val applicationRepository: ApplicationRepository,
        private val instanceManagerService: InstanceManagerService
) : ApplicationService {

    override fun createApplication(name: String, visibility: Visibility, managerName: InstanceManagerName): Long {
        //TODO check name empty
        //TODO check name not unique
        val newApplication = Application(0, name, visibility, managerName)

        applicationRepository.save(newApplication)
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


}