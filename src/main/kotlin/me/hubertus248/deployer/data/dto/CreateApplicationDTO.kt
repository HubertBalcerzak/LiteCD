package me.hubertus248.deployer.data.dto

import me.hubertus248.deployer.data.entity.Visibility
import me.hubertus248.deployer.instance.InstanceManagerName

//TODO application type etc.
class CreateApplicationDTO(
        val name: String,
        val visibility: Visibility,
        val manager: InstanceManagerName
)