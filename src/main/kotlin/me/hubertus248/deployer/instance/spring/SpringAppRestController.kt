package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.data.dto.EnvironmentDTO
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.data.entity.Secret
import me.hubertus248.deployer.exception.BadRequestException
import me.hubertus248.deployer.instance.spring.instance.AvailableSpringInstanceService
import me.hubertus248.deployer.security.Authenticated
import org.springframework.stereotype.Controller
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

    @Authenticated
    @PostMapping("/spring/saveApplicationEnv/{appId}")
    fun updateDefaultEnvironment(@PathVariable appId: Long, @RequestBody environment: EnvironmentDTO) {
        springEnvironmentService.updateApplicationEnvironment(appId, environment)
    }

    @Authenticated
    @PostMapping("/spring/saveInstanceEnv/{instanceId}")
    fun updateInstanceEnvironment() {
        TODO()
    }

    @Authenticated
    @PostMapping("/spring/saveInstanceSubdomain/{instanceId}")
    fun updateInstanceSubdomain() {
        TODO()
    }
}