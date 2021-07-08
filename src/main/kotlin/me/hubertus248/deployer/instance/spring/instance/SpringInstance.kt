package me.hubertus248.deployer.instance.spring.instance

import me.hubertus248.deployer.data.entity.*
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.OneToMany
import javax.persistence.OneToOne

@Entity
class SpringInstance(
    @OneToOne
    val workspace: Workspace,

    @OneToOne
    var process: SubProcess?,

    @Embedded
    var subdomain: DomainLabel,

    @Embedded
    var zuulMappingId: ZuulMappingId?,

    environment: MutableSet<EnvironmentVariable>,

    @Embedded
    var port: Port?,

    key: InstanceKey,
    application: SpringApplication,
    status: InstanceStatus = InstanceStatus.STOPPED
) : InstanceWithEnvironment(environment, key, application, status)