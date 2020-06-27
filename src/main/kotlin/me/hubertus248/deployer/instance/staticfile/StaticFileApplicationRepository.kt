package me.hubertus248.deployer.instance.staticfile

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StaticFileApplicationRepository : JpaRepository<StaticFileApplication, Long> {

    fun findFirstById(applicationId: Long): StaticFileApplication?
}