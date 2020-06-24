package me.hubertus248.deployer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DeployerApplication

fun main(args: Array<String>) {
	runApplication<DeployerApplication>(*args)
}
