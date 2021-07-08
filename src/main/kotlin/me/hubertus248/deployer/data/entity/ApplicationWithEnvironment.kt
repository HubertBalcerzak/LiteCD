package me.hubertus248.deployer.data.entity

import me.hubertus248.deployer.instance.InstanceManagerName
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class ApplicationWithEnvironment(
    name: ApplicationName,
    visibility: Visibility,
    instanceManagerName: InstanceManagerName,

    @OneToMany(fetch = FetchType.LAZY)
    val defaultEnvironment: MutableSet<EnvironmentVariable> = mutableSetOf()
) : Application(0, name, visibility, instanceManagerName)