package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.data.entity.FilesystemFileMetadata
import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.data.entity.InstanceKey
import javax.persistence.*

@Entity
class StaticFileInstance(
        @OneToOne
        var fileMetadata: FilesystemFileMetadata,

        staticFileApplication: StaticFileApplication,
        instanceKey: InstanceKey
) : Instance(0, instanceKey, staticFileApplication)