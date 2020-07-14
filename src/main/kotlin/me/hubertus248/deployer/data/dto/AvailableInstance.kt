package me.hubertus248.deployer.data.dto

import me.hubertus248.deployer.data.entity.InstanceKey
import java.time.LocalDateTime

data class AvailableInstance(
        val key: InstanceKey,
        val updated: LocalDateTime
)