package me.hubertus248.deployer.instance.staticfile

import org.springframework.data.jpa.repository.JpaRepository

interface StaticFileApplicationRepository : JpaRepository<StaticFileApplication, Long> {
}