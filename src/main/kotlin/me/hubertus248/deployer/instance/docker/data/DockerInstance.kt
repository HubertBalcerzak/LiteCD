package me.hubertus248.deployer.instance.docker.data

import me.hubertus248.deployer.data.entity.*
import javax.persistence.Embedded
import javax.persistence.Entity

@Entity
class DockerInstance(
    var subdomain: DomainLabel,

    var zuulMappingId: ZuulMappingId?,

    environment: MutableSet<EnvironmentVariable>,

    @Embedded
    var port: Port?,

    key: InstanceKey,
    application: DockerApplication,
    status: InstanceStatus = InstanceStatus.STOPPED
) : InstanceWithEnvironment(environment, key, application, status)