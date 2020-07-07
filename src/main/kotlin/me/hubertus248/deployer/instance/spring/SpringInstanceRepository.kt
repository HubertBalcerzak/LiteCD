package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.instance.staticfile.StaticFileInstance
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringInstanceRepository : JpaRepository<SpringInstance, Long> {
    fun findAllById(appId: Long, pageable: Pageable): List<SpringInstance>

}