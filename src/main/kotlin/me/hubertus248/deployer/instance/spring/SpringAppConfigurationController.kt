package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.exception.NotFoundException
import me.hubertus248.deployer.instance.spring.application.SpringApplicationRepository
import me.hubertus248.deployer.security.IsAdmin
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/spring")
class SpringAppConfigurationController(
        private val springApplicationRepository: SpringApplicationRepository,
        private val springEnvironmentService: SpringEnvironmentService
) {

    @IsAdmin
    @GetMapping("/configureApp/{appId}")
    fun configureSpringApp(@PathVariable appId: Long, model: Model): String {
        val application = springApplicationRepository.findFirstById(appId) ?: throw NotFoundException()
        model.addAttribute("app", application)
        model.addAttribute("env", springEnvironmentService.getEnvironment(application))
        return "application/spring/springApplicationConfiguration"
    }

    @IsAdmin
    @GetMapping("/configureInstance/{instanceId}")
    fun configureSpringInstance(@PathVariable instanceId: Long): String {
        TODO()
    }
}