package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.data.entity.FilesystemFileMetadata
import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.data.entity.InstanceKey
import javax.persistence.*

@Entity
class StaticFileInstance(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @OneToOne
        var fileMetadata: FilesystemFileMetadata,

        @Embedded
        val key: InstanceKey,

        @ManyToOne
        val staticFileApplication: StaticFileApplication
) : Instance {
    override fun getName(): String = key.value
}