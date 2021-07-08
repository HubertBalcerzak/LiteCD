package me.hubertus248.deployer.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated

@Configuration
@ConstructorBinding
@ConfigurationProperties(prefix = "litecd")
@Validated
class LitecdProperties(
    val domain: String,
    val protocol: String
)