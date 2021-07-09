package me.hubertus248.deployer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@EnableZuulProxy
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@ConfigurationPropertiesScan("me.hubertus248.deployer")
@SpringBootApplication
class DeployerApplication

fun main(args: Array<String>) {
	runApplication<DeployerApplication>(*args)
}
