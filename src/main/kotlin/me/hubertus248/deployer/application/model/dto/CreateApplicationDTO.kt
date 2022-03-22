package me.hubertus248.deployer.application.model.dto

import me.hubertus248.deployer.application.model.entity.Visibility
import me.hubertus248.deployer.instancemanager.InstanceManagerName

class CreateApplicationDTO(
    val name: String,
    val visibility: Visibility,
    val manager: InstanceManagerName
)