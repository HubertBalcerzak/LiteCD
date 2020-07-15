package me.hubertus248.deployer.controller

import me.hubertus248.deployer.exception.NotFoundException
import me.hubertus248.deployer.data.dto.CreateApplicationDTO
import me.hubertus248.deployer.data.entity.ApplicationName
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.instance.InstanceManagerFeature
import me.hubertus248.deployer.security.Authenticated
import me.hubertus248.deployer.service.ApplicationService
import me.hubertus248.deployer.service.InstanceManagerService
import me.hubertus248.deployer.service.InstanceService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.security.Principal

@Controller
class ApplicationController(
        private val applicationService: ApplicationService,
        private val instanceManagerService: InstanceManagerService,
        private val instanceService: InstanceService
) {

    @GetMapping("/apps")
    fun applicationList(principal: Principal?, pageable: Pageable, model: Model): String {
        if (principal == null) model.addAttribute("applications",
                applicationService.listPublicApplications(pageable))
        else
            model.addAttribute("applications", applicationService.listApplications(pageable))

        return "apps"
    }

    //TODO restrict to admin
    @Authenticated
    @GetMapping("/newApp")
    fun newApp(model: Model): String {
        model.addAttribute("managers", instanceManagerService.getAvailableManagers())
        return "newApp"
    }

    //TODO restrict to admin
    @Authenticated
    @PostMapping("/newApp")
    fun newAppPost(@ModelAttribute @Validated createApplicationDTO: CreateApplicationDTO): RedirectView {
        applicationService.createApplication(ApplicationName(createApplicationDTO.name),
                createApplicationDTO.visibility,
                createApplicationDTO.manager)
        return RedirectView("apps")//TODO redirect to app page
    }

    @GetMapping("/app/{appId}")
    fun getApp(@PathVariable appId: Long, model: Model, authentication: Authentication?): String {
        val application = applicationService.findApplication(appId, authentication?.isAuthenticated ?: false)
                ?: throw NotFoundException()
        val instanceManager = instanceManagerService.getManagerForApplication(application)

        model.addAttribute("app", application)
        model.addAttribute("instanceManager", instanceManager)
        model.addAttribute("instances", instanceManager.listInstances(application.id, PageRequest.of(0, 50)))//TODO pagination

        if (instanceManager.supportsFeature(InstanceManagerFeature.POSSIBLE_INSTANCE_LIST)) {
            model.addAttribute("possibleInstances",
                    instanceManager.getPossibleInstanceList(application.id, PageRequest.of(0, 50)))//TODO pagination
        }
        return "app"
    }

    //TODO restrict to admin/user (?)
    @Authenticated
    @PostMapping("/app/{appId}/start")
    fun start(@PathVariable appId: Long, @RequestParam key: String): RedirectView {
        instanceService.createAndStart(appId, InstanceKey(key))
        return RedirectView("/app/$appId")
    }
}