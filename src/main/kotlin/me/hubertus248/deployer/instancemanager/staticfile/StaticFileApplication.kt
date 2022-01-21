package me.hubertus248.deployer.instancemanager.staticfile

import me.hubertus248.deployer.applications.model.entity.Application
import me.hubertus248.deployer.applications.model.entity.ApplicationName
import me.hubertus248.deployer.data.entity.Secret
import me.hubertus248.deployer.applications.model.entity.Visibility
import javax.persistence.Embedded
import javax.persistence.Entity

@Entity
class StaticFileApplication(
    @Embedded
        val secret: Secret,

    name: ApplicationName,
    visibility: Visibility
) : Application(0, name, visibility, INSTANCE_MANAGER_STATIC_FILE_NAME/*, subdomains = false*/)