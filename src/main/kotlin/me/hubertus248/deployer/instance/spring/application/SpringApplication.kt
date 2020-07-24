package me.hubertus248.deployer.instance.spring.application

import me.hubertus248.deployer.data.entity.*
import me.hubertus248.deployer.instance.spring.INSTANCE_MANAGER_SPRING_NAME
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany


@Entity
class SpringApplication(
        @Embedded
        val secret: Secret,

        name: ApplicationName,
        visibility: Visibility,

        @OneToMany(fetch = FetchType.LAZY)
        val defaultEnvironment: MutableSet<EnvironmentVariable> = mutableSetOf()
) : Application(0, name, visibility, INSTANCE_MANAGER_SPRING_NAME)