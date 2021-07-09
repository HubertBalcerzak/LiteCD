package me.hubertus248.deployer.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@ConstructorBinding
@ConfigurationProperties(prefix = "litecd")
class LitecdProperties(
    val domain: String,
    val protocol: String
)