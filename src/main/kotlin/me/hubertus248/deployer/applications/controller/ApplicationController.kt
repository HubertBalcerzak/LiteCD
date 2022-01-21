package me.hubertus248.deployer.applications.controller

import me.hubertus248.deployer.applications.mapper.ApplicationMapper
import me.hubertus248.deployer.applications.model.dto.ApplicationDTO
import me.hubertus248.deployer.applications.model.entity.Application
import me.hubertus248.deployer.data.dto.CreateApplicationDTO
import me.hubertus248.deployer.applications.model.entity.ApplicationName
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.exception.NotFoundException
import me.hubertus248.deployer.instancemanager.InstanceManagerFeature
import me.hubertus248.deployer.security.IsAdmin
import me.hubertus248.deployer.applications.service.ApplicationService
import me.hubertus248.deployer.service.InstanceManagerService
import me.hubertus248.deployer.service.InstanceService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView
import java.security.Principal

//todo write tests
@RestController
class ApplicationController(
    private val applicationService: ApplicationService,
    private val applicationMapper: ApplicationMapper
) {

    @GetMapping("/apps")
    fun applicationList(
        principal: Principal?,
        @PageableDefault(size = 10) pageable: Pageable
    ): Page<ApplicationDTO> {
        return if (principal == null) {
            applicationService.listPublicApplications(pageable)
                .map(applicationMapper::toApplicationDTO)
        } else {
            applicationService.listApplications(pageable)
                .map(applicationMapper::toApplicationDTO)
        }
    }

    @IsAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/app/{appId}")
    fun deleteApplication(@PathVariable appId: Long) {
        applicationService.deleteApplication(appId)
    }

    @IsAdmin
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/newApp")
    fun createApplication(@RequestBody @Validated createApplicationDTO: CreateApplicationDTO): ApplicationDTO {
        return applicationService.createApplication(
            ApplicationName(createApplicationDTO.name),
            createApplicationDTO.visibility,
            createApplicationDTO.manager
        ).let(applicationMapper::toApplicationDTO)
    }

    @GetMapping("/app/{appId}")
    fun getApp(@PathVariable appId: Long, authentication: Authentication?): ApplicationDTO {
        return applicationService.findApplication(appId, authentication?.isAuthenticated ?: false)
            ?.let(applicationMapper::toApplicationDTO) ?: throw NotFoundException()
    }

}