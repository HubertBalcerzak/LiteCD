package me.hubertus248.deployer.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.util.unit.DataSize

@ConstructorBinding
@ConfigurationProperties("deployer")
class DeployerProperties(
    val maxFileSize: DataSize
)
