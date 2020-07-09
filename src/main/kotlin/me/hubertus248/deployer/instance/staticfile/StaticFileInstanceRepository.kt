package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.reposiotry.InstanceRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface StaticFileInstanceRepository : InstanceRepository<StaticFileInstance> {

    fun findFirstById(id: Long): StaticFileInstance?
}