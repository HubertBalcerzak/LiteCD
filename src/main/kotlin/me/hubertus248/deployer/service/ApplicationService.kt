package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.Visibility
import me.hubertus248.deployer.reposiotry.ApplicationRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

interface ApplicationService {
    fun createApplication(name: String, visibility: Visibility): Long

    fun listApplications(pageable: Pageable): Set<Application>

    fun listPublicApplications(pageable: Pageable): Set<Application>
}

@Service
class ApplicationServiceImpl(
        private val applicationRepository: ApplicationRepository
) : ApplicationService {

    override fun createApplication(name: String, visibility: Visibility): Long {
        val newApplication = Application(0, name, visibility)

        applicationRepository.save(newApplication)
        return newApplication.id
    }

    override fun listApplications(pageable: Pageable): Set<Application> {
        return applicationRepository.findAll(pageable).toSet()
    }

    override fun listPublicApplications(pageable: Pageable): Set<Application> {
        return applicationRepository.findAllPublic(pageable).toSet()
    }


}