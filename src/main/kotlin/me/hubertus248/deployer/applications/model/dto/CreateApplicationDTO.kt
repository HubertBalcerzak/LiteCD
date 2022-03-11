package me.hubertus248.deployer.applications.model.dto

import me.hubertus248.deployer.applications.model.entity.Visibility
import me.hubertus248.deployer.instancemanager.InstanceManagerName

class CreateApplicationDTO(
    val name: String,
    val visibility: Visibility,
    val manager: InstanceManagerName
)