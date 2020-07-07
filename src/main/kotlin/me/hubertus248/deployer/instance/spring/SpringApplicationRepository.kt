package me.hubertus248.deployer.instance.spring

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringApplicationRepository : JpaRepository<SpringApplication, Long> {

}