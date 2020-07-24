package me.hubertus248.deployer.data.dto

class EnvironmentDTO(
        val variables: List<EnvironmentVariableDTO>
)

class EnvironmentVariableDTO(
        var name: String,
        var value: String
)