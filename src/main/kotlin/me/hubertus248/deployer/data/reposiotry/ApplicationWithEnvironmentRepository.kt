package me.hubertus248.deployer.data.reposiotry

import me.hubertus248.deployer.data.entity.ApplicationWithEnvironment
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ApplicationWithEnvironmentRepository : JpaRepository<ApplicationWithEnvironment, Long> {

    fun findFirstById(applicationId: Long): ApplicationWithEnvironment?
}