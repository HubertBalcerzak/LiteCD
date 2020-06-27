package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.ApplicationName
import me.hubertus248.deployer.data.entity.Visibility
import me.hubertus248.deployer.instance.InstanceManagerName
import javax.persistence.*

@Entity
class StaticFileApplication(
        @Embedded
        val secret: Secret,

        name: ApplicationName,
        visibility: Visibility
) : Application(0, name, visibility, INSTANCE_MANAGER_STATIC_FILE_NAME)

@Embeddable
data class Secret(
        @Column(length = 255, updatable = true, unique = false, nullable = false)
        var secret: String
)