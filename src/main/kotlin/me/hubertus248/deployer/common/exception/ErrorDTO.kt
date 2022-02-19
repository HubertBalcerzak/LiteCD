package me.hubertus248.deployer.common.exception

class ErrorDTO(
    val message: String,
    val details: List<String> = emptyList()
)