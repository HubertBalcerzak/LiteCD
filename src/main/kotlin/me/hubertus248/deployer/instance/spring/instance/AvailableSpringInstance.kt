package me.hubertus248.deployer.instance.spring.instance

import me.hubertus248.deployer.data.entity.FilesystemFileMetadata
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class AvailableSpringInstance(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @OneToOne
        var artifact: FilesystemFileMetadata,

        @Embedded
        val key: InstanceKey,

        @ManyToOne
        val application: SpringApplication,

        @Column
        val creationTime: LocalDateTime,

        @Column
        var lastUpdate: LocalDateTime

)