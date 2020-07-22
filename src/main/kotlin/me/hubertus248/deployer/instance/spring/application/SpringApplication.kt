package me.hubertus248.deployer.instance.spring.application

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.ApplicationName
import me.hubertus248.deployer.data.entity.Secret
import me.hubertus248.deployer.data.entity.Visibility
import me.hubertus248.deployer.instance.spring.INSTANCE_MANAGER_SPRING_NAME
import javax.persistence.Embedded
import javax.persistence.Entity


@Entity
class SpringApplication(
        @Embedded
        val secret: Secret,

        name: ApplicationName,
        visibility: Visibility
) : Application(0, name, visibility, INSTANCE_MANAGER_SPRING_NAME/*, subdomains = true*/)