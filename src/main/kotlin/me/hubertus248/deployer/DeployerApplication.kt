package me.hubertus248.deployer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@ConfigurationPropertiesScan(basePackages = ["me.hubertus248.deployer"])
@EnableZuulProxy
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@SpringBootApplication(exclude = [ErrorMvcAutoConfiguration::class])
class DeployerApplication

fun main(args: Array<String>) {
	runApplication<DeployerApplication>(*args)
}
