package me.hubertus248.deployer.instance.docker.repository

import me.hubertus248.deployer.instance.docker.data.DockerApplication
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DockerApplicationRepository : JpaRepository<DockerApplication, Long> {

    fun findFirstById(applicationId: Long): DockerApplication?
}