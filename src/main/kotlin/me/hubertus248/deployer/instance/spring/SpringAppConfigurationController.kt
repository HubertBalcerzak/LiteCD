package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.data.entity.DomainLabel
import me.hubertus248.deployer.exception.NotFoundException
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import me.hubertus248.deployer.instance.spring.application.SpringApplicationRepository
import me.hubertus248.deployer.instance.spring.instance.SpringInstanceRepository
import me.hubertus248.deployer.security.IsAdmin
import me.hubertus248.deployer.service.InstanceManagerService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@Controller
@RequestMapping("/spring")
class SpringAppConfigurationController(
        private val springApplicationRepository: SpringApplicationRepository,
        private val springEnvironmentService: SpringEnvironmentService,
        private val springInstanceRepository: SpringInstanceRepository,
        private val springInstanceManager: SpringInstanceManager
) {

    @Value("\${deployer.domain}")
    private val domain: String = ""

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
    fun configureSpringInstance(@PathVariable instanceId: Long, model: Model): String {
        val instance = springInstanceRepository.findFirstById(instanceId) ?: throw NotFoundException()
        model.addAttribute("instance", instance)
        model.addAttribute("app", instance.application as SpringApplication)
        model.addAttribute("instanceManager", springInstanceManager)
        model.addAttribute("domain", domain)
        model.addAttribute("env", springEnvironmentService.getRawEnvironment(instance))
        return "application/spring/springInstancePage"
    }

    @IsAdmin
    @PostMapping("/configureSubdomain/{instanceId}")
    fun updateInstanceSubdomain(@PathVariable instanceId: Long, @RequestParam subdomain: String): RedirectView {
        springInstanceManager.updateSubdomain(instanceId, DomainLabel(subdomain))
        return RedirectView("/spring/configureInstance/$instanceId")
    }
}