package me.hubertus248.deployer.application.model.entity

import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.instancemanager.InstanceManagerName
import java.time.Instant
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Application(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long,

    @Embedded
    open val name: ApplicationName,

    @Column(unique = false, updatable = true)
    open val visibility: Visibility,

    @Embedded
    open val manager: InstanceManagerName,

    @OneToMany(fetch = FetchType.LAZY)
    open val instances: Set<Instance> = emptySet(),

    @Column
    open val creationDateTime: Instant = Instant.now()
)
