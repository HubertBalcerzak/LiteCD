package me.hubertus248.deployer.instance.docker.repository

import me.hubertus248.deployer.data.reposiotry.InstanceRepository
import me.hubertus248.deployer.instance.docker.data.DockerInstance
import org.springframework.stereotype.Repository

@Repository
interface DockerInstanceRepository : InstanceRepository<DockerInstance> {

}