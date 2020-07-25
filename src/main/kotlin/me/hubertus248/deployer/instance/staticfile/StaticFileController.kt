package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.data.entity.Secret
import me.hubertus248.deployer.exception.BadRequestException
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import javax.servlet.http.HttpServletResponse

@RestController
class StaticFileController(
        private val staticFileInstanceManager: StaticFileInstanceManager
) {

    @PostMapping("/api/pub/staticfile/create")
    fun createInstance(@RequestParam app: Long,
                       @RequestParam key: String,
                       @RequestParam file: MultipartFile,
                       @RequestParam secret: String?,
                       @RequestHeader("secret") secretHeader: String?) {
        val actualSecret = Secret(secret ?: secretHeader ?: throw BadRequestException())

        staticFileInstanceManager.createInstance(app, actualSecret, file, InstanceKey(key))
    }

    @GetMapping("/open/staticfile/{instanceId}")
    fun openInstance(@PathVariable instanceId: Long, authentication: Authentication, httpServletResponse: HttpServletResponse) {
//TODO require authentication for restricted apps

        staticFileInstanceManager.openInstance(instanceId, httpServletResponse)
    }
}