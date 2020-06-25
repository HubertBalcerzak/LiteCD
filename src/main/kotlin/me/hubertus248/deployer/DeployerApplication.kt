package me.hubertus248.deployer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@SpringBootApplication
class DeployerApplication

fun main(args: Array<String>) {
	runApplication<DeployerApplication>(*args)
}
