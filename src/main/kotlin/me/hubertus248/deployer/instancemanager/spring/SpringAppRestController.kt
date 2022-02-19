package me.hubertus248.deployer.instancemanager.spring

import me.hubertus248.deployer.data.dto.EnvironmentDTO
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.data.entity.Secret
import me.hubertus248.deployer.common.exception.BadRequestException
import me.hubertus248.deployer.instancemanager.spring.instance.AvailableSpringInstanceService
import me.hubertus248.deployer.security.IsAdmin
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
class SpringAppRestController(
        private val availableSpringInstanceService: AvailableSpringInstanceService,
        private val springEnvironmentService: SpringEnvironmentService
) {

    @PostMapping("/api/pub/spring/add")
    fun uploadArtifact(@RequestParam app: Long,
                       @RequestParam key: String,
                       @RequestParam file: MultipartFile,
                       @RequestParam secret: String?,
                       @RequestHeader("secret") secretHeader: String?) {

        val actualSecret = Secret(secret ?: secretHeader ?: throw BadRequestException())

        availableSpringInstanceService.addArtifact(app, actualSecret, file, InstanceKey(key))
    }

    @IsAdmin
    @PostMapping("/spring/saveApplicationEnv/{appId}")
    fun updateDefaultEnvironment(@PathVariable appId: Long, @RequestBody environment: EnvironmentDTO) {
        springEnvironmentService.updateApplicationEnvironment(appId, environment)
    }

    @IsAdmin
    @PostMapping("/spring/saveInstanceEnv/{instanceId}")
    fun updateInstanceEnvironment(@PathVariable instanceId: Long, @RequestBody environment: EnvironmentDTO) {
        springEnvironmentService.updateInstanceEnvironment(instanceId, environment)
    }
}