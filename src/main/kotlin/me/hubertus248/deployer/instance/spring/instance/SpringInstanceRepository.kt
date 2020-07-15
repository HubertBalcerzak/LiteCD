package me.hubertus248.deployer.instance.spring.instance

import me.hubertus248.deployer.data.reposiotry.InstanceRepository
import org.springframework.stereotype.Repository

@Repository
interface SpringInstanceRepository : InstanceRepository<SpringInstance> {

}