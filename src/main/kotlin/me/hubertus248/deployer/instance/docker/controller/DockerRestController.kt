package me.hubertus248.deployer.instance.docker.controller

import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.data.entity.Secret
import me.hubertus248.deployer.exception.BadRequestException
import me.hubertus248.deployer.instance.docker.service.AvailableDockerInstanceService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class DockerRestController(
    private val availableDockerInstanceService: AvailableDockerInstanceService
) {

    @PostMapping("/api/pub/docker/add")
    fun registerInstance(
        @RequestParam app: Long,
        @RequestParam key: String,
        @RequestParam secret: String?,
        @RequestHeader("secret") secretHeader: String?
    ) {
        val actualSecret = Secret(secret ?: secretHeader ?: throw BadRequestException())

        availableDockerInstanceService.registerInstance(app, actualSecret, InstanceKey(key))
    }
}