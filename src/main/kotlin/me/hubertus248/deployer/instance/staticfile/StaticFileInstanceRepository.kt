package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.data.reposiotry.InstanceRepository
import org.springframework.stereotype.Repository

@Repository
interface StaticFileInstanceRepository : InstanceRepository<StaticFileInstance>