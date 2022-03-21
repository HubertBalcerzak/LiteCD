package me.hubertus248.deployer.security

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.context.annotation.Bean

@Qualifier("exampleBean")
@ConstructorBinding
@ConfigurationProperties(prefix = "deployer.security")
data class SecurityProperties(
    val oauthScope: String,
    val includeClientRoles: Boolean,
    val includeRealmRoles: Boolean,
    val clientName: String,
    val adminRole: String
) {
    @Bean
    fun oauthConfig() = OauthConfig(oauthScope, adminRole)

    data class OauthConfig(val oauthScope: String, val adminRole: String)
}