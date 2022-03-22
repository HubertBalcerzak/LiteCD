package me.hubertus248.deployer.application.model.dto

import me.hubertus248.deployer.application.model.entity.Visibility
import java.time.Instant

class ApplicationDTO(
    val id: Long,
    val name: String,
    val visibility: Visibility,
    val manager: String,
    val creationDateTime: Instant
)