package me.hubertus248.deployer.instance.spring.application

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringApplicationRepository : JpaRepository<SpringApplication, Long> {
    fun findFirstById(id: Long): SpringApplication?
}