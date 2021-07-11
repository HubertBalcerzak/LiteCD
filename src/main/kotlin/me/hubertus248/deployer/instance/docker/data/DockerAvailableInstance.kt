package me.hubertus248.deployer.instance.docker.data

import me.hubertus248.deployer.data.entity.InstanceKey
import java.time.Instant
import javax.persistence.*

@Entity
class AvailableDockerInstance(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Embedded
    val key: InstanceKey,

    @ManyToOne
    val application: DockerApplication,

    val creationTime: Instant,

    var lastUpdate: Instant,

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    var actualInstance: DockerInstance? = null,

    var deleted: Boolean = false
)