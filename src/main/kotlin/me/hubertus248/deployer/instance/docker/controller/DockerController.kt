package me.hubertus248.deployer.instance.docker.controller

import me.hubertus248.deployer.configuration.LitecdProperties
import me.hubertus248.deployer.exception.NotFoundException
import me.hubertus248.deployer.instance.docker.DockerInstanceManager
import me.hubertus248.deployer.instance.docker.data.DockerApplication
import me.hubertus248.deployer.instance.docker.repository.DockerApplicationRepository
import me.hubertus248.deployer.instance.docker.repository.DockerInstanceRepository
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import me.hubertus248.deployer.security.IsAdmin
import me.hubertus248.deployer.service.EnvironmentService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/docker")
class DockerController(
    private val dockerApplicationRepository: DockerApplicationRepository,
    private val dockerInstanceRepository: DockerInstanceRepository,
    private val environmentService: EnvironmentService,
    private val dockerInstanceManager: DockerInstanceManager,
    private val litecdProperties: LitecdProperties
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @IsAdmin
    @GetMapping("/configureApp/{appId}")
    fun configureSpringApp(@PathVariable appId: Long, model: Model): String {
        val application = dockerApplicationRepository.findFirstById(appId) ?: throw NotFoundException()
        model.addAttribute("app", application)
        model.addAttribute("env", environmentService.getEnvironment(application))
        return "application/docker/dockerApplicationConfiguration"
    }

    @IsAdmin
    @GetMapping("/configureInstance/{instanceId}")
    fun configureSpringInstance(@PathVariable instanceId: Long, model: Model): String {
        val instance = dockerInstanceRepository.findFirstById(instanceId) ?: throw NotFoundException()
        model.addAttribute("instance", instance)
        model.addAttribute("app", instance.application as DockerApplication)
        model.addAttribute("instanceManager", dockerInstanceManager)
        model.addAttribute("domain", litecdProperties.domain)
        model.addAttribute("env", environmentService.getRawEnvironment(instance))
        //todo get logs
//        model.addAttribute("logs", logService.getRecentLogs(instance.workspace))
        return "application/docker/dockerInstancePage"
    }

//    @IsAdmin
//    @PostMapping
//    fun configureDocker(){
//
//    }
}