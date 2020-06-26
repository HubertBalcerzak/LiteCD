package me.hubertus248.deployer.controller

import me.hubertus248.deployer.data.dto.CreateApplicationDTO
import me.hubertus248.deployer.service.ApplicationService
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
class ApplicationRestController(
        private val applicationService: ApplicationService
) {

    @GetMapping("/api/apps")
    fun listApplications(principal: Principal?) {
        println(principal)
        //TODO()
    }

    //TODO restrict to admin
    @PutMapping("/api/apps")
    fun createApplication(@RequestBody createApplicationDTO: CreateApplicationDTO) {
        applicationService.createApplication(createApplicationDTO.name,
                createApplicationDTO.visibility,
                createApplicationDTO.manager)
    }
}