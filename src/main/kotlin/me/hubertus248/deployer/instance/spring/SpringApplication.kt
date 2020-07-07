package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.ApplicationName
import me.hubertus248.deployer.data.entity.Visibility
import javax.persistence.Entity


@Entity
class SpringApplication(
        name: ApplicationName,
        visibility: Visibility
) : Application(0, name, visibility, INSTANCE_MANAGER_SPRING_NAME)