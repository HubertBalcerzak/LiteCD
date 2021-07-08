package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.data.entity.DomainLabel
import me.hubertus248.deployer.exception.NotFoundException
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import me.hubertus248.deployer.instance.spring.application.SpringApplicationRepository
import me.hubertus248.deployer.instance.spring.instance.SpringInstanceRepository
import me.hubertus248.deployer.security.IsAdmin
import me.hubertus248.deployer.service.LogService
import me.hubertus248.deployer.service.EnvironmentService
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ContentDisposition
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.io.IOException
import java.nio.charset.Charset
import javax.servlet.http.HttpServletResponse

@Controller
@RequestMapping("/spring")
class SpringAppConfigurationController(
    private val springApplicationRepository: SpringApplicationRepository,
    private val environmentService: EnvironmentService,
    private val springInstanceRepository: SpringInstanceRepository,
    private val springInstanceManager: SpringInstanceManager,
    private val logService: LogService
) {

    @Value("\${deployer.domain}")
    private val domain: String = ""

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @IsAdmin
    @GetMapping("/configureApp/{appId}")
    fun configureSpringApp(@PathVariable appId: Long, model: Model): String {
        val application = springApplicationRepository.findFirstById(appId) ?: throw NotFoundException()
        model.addAttribute("app", application)
        model.addAttribute("env", environmentService.getEnvironment(application))
        return "application/spring/springApplicationConfiguration"
    }

    @IsAdmin
    @GetMapping("/configureInstance/{instanceId}")
    fun configureSpringInstance(@PathVariable instanceId: Long, model: Model): String {
        val instance = springInstanceRepository.findFirstById(instanceId) ?: throw NotFoundException()
        model.addAttribute("instance", instance)
        model.addAttribute("app", instance.application as SpringApplication)
        model.addAttribute("instanceManager", springInstanceManager)
        model.addAttribute("domain", domain)
        model.addAttribute("env", environmentService.getRawEnvironment(instance))
        model.addAttribute("logs", logService.getRecentLogs(instance.workspace))
        return "application/spring/springInstancePage"
    }

    @IsAdmin
    @PostMapping("/configureSubdomain/{instanceId}")
    fun updateInstanceSubdomain(@PathVariable instanceId: Long, @RequestParam subdomain: String): RedirectView {
        springInstanceManager.updateSubdomain(instanceId, DomainLabel(subdomain))
        return RedirectView("/spring/configureInstance/$instanceId")
    }

    @IsAdmin
    @GetMapping("/logfile/{instanceId}")
    fun getLogFile(@PathVariable instanceId: Long, response: HttpServletResponse) {
        val instance = springInstanceRepository.findFirstById(instanceId) ?: throw NotFoundException()
        val logFile = logService.getLogFile(instance.workspace)

        response.contentType = "text/plain"
        response.addHeader("Content-Disposition",
                ContentDisposition.builder("attachment")
                        .filename("log.txt", Charset.forName("UTF-8")) //TODO add date
                        .build()
                        .toString())
        if (logFile != null) {

            val stream = logFile.inputStream()
            try {
                IOUtils.copyLarge(stream, response.outputStream)
            } catch (e: IOException) {
                logger.debug(e.toString())
            } finally {
                stream.close()
                response.outputStream.flush()
            }
        }
    }
}