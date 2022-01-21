package me.hubertus248.deployer.applications.model.dto

import me.hubertus248.deployer.applications.model.entity.Visibility
import java.time.Instant

class ApplicationDTO(
    val id: Long,
    val name: String,
    val visibility: Visibility,
    val manager: String,
    val creationDateTime: Instant
)