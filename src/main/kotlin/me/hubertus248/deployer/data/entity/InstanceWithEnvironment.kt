package me.hubertus248.deployer.data.entity

import javax.persistence.Entity
import javax.persistence.OneToMany

@Entity
abstract class InstanceWithEnvironment(

    @OneToMany
    open val environment: MutableSet<EnvironmentVariable>,

    key: InstanceKey,
    application: ApplicationWithEnvironment,
    status: InstanceStatus = InstanceStatus.STOPPED
) : Instance(0, key, application, status)