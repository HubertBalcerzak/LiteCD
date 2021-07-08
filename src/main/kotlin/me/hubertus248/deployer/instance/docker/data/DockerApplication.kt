package me.hubertus248.deployer.instance.docker.data

import me.hubertus248.deployer.data.entity.*
import me.hubertus248.deployer.instance.spring.INSTANCE_MANAGER_SPRING_NAME
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity
class DockerApplication(

    @Embedded
    var registryUsername: RegistryUsername?,

    @Embedded
    var registryPassword: RegistryPassword?,

    @Embedded
    val imageName: ImageName?,

    name: ApplicationName,
    visibility: Visibility,
) : ApplicationWithEnvironment(name, visibility, INSTANCE_MANAGER_SPRING_NAME)