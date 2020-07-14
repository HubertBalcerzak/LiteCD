package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.data.entity.Secret
import me.hubertus248.deployer.exception.BadRequestException
import me.hubertus248.deployer.instance.spring.instance.AvailableSpringInstanceService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class SpringAppController(
        private val availableSpringInstanceService: AvailableSpringInstanceService
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
}