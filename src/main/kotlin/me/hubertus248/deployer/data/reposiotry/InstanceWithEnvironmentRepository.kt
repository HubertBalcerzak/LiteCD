package me.hubertus248.deployer.data.reposiotry

import me.hubertus248.deployer.data.entity.InstanceWithEnvironment
import org.springframework.stereotype.Repository

@Repository
interface InstanceWithEnvironmentRepository : InstanceRepository<InstanceWithEnvironment>