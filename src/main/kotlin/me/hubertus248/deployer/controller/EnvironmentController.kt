package me.hubertus248.deployer.controller

import me.hubertus248.deployer.data.dto.EnvironmentDTO
import me.hubertus248.deployer.security.IsAdmin
import me.hubertus248.deployer.service.EnvironmentService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/env")
class EnvironmentController(
    private val environmentService: EnvironmentService
) {

    @IsAdmin
    @PostMapping("/application/{appId}")
    fun updateDefaultEnvironment(@PathVariable appId: Long, @RequestBody environment: EnvironmentDTO) {
        environmentService.updateApplicationEnvironment(appId, environment)
    }

    @IsAdmin
    @PostMapping("/instance/{instanceId}")
    fun updateInstanceEnvironment(@PathVariable instanceId: Long, @RequestBody environment: EnvironmentDTO) {
        environmentService.updateInstanceEnvironment(instanceId, environment)
    }
}