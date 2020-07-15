package me.hubertus248.deployer.instance.spring.instance

import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.data.entity.Workspace
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import javax.persistence.Entity
import javax.persistence.OneToOne

@Entity
class SpringInstance(
        @OneToOne
        val workspace: Workspace,
        key: InstanceKey,
        application: SpringApplication
) : Instance(0, key, application)