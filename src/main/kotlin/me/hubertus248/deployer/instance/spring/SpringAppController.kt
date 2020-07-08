package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.data.entity.Secret
import me.hubertus248.deployer.exception.BadRequestException
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
class SpringAppController() {

    @PostMapping("/api/pub/staticfile/create/add")
    fun uploadArtifact(@RequestParam app: Long,
                       @RequestParam key: String,
                       @RequestParam file: MultipartFile,
                       @RequestParam secret: String?,
                       @RequestHeader("secret") secretHeader: String?) {

        val actualSecret = Secret(secret ?: secretHeader ?: throw BadRequestException())

    }
}