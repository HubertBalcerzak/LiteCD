package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.BadRequestException
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class StaticFileController(
        private val staticFileInstanceManager: StaticFileInstanceManager
) {

    @PostMapping("/api/staticfile/create")
    fun createInstance(@RequestParam app: Long,
                       @RequestParam key: String,
                       @RequestParam file: MultipartFile,
                       @RequestParam secret: String?,
                       @RequestHeader("secret") secretHeader: String?) {
        val actualSecret: String = secret ?: secretHeader ?: throw BadRequestException()

        staticFileInstanceManager.createInstance(app, actualSecret, file, key)
    }
}