package me.hubertus248.deployer.instance.spring.instance

import me.hubertus248.deployer.data.entity.*
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import javax.persistence.Entity
import javax.persistence.OneToOne

@Entity
class SpringInstance(
        @OneToOne
        val workspace: Workspace,

        @OneToOne
        val process: SubProcess?,

        key: InstanceKey,
        application: SpringApplication,
        status: InstanceStatus = InstanceStatus.STOPPED
) : Instance(0, key, application, status)