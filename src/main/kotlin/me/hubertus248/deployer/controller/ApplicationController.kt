package me.hubertus248.deployer.controller

import me.hubertus248.deployer.service.ApplicationService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import java.security.Principal

@Controller
class ApplicationController(
        private val applicationService: ApplicationService
) {

    @GetMapping("/apps")
    fun applicationList(principal: Principal?, pageable: Pageable, model: Model): String {
        if (principal == null) model.addAttribute("applications",
                applicationService.listPublicApplications(pageable))
        else
            model.addAttribute("applications", applicationService.listApplications(pageable))

        return "apps"
    }
}