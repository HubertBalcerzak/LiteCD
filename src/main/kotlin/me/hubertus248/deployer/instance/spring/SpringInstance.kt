package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.data.entity.InstanceKey
import javax.persistence.Entity

@Entity
class SpringInstance(
        key: InstanceKey
) : Instance(0, key)