package me.hubertus248.deployer.instance.spring.instance

import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import javax.persistence.Entity

@Entity
class SpringInstance(
        key: InstanceKey,
        application: SpringApplication
) : Instance(0, key, application)