package me.hubertus248.deployer.instance.spring.instance

import me.hubertus248.deployer.reposiotry.InstanceRepository
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringInstanceRepository : InstanceRepository<SpringInstance> {

}