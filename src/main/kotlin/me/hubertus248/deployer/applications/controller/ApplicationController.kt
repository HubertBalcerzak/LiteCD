package me.hubertus248.deployer.applications.controller

import me.hubertus248.deployer.applications.mapper.ApplicationMapper
import me.hubertus248.deployer.applications.model.dto.ApplicationDTO
import me.hubertus248.deployer.data.dto.CreateApplicationDTO
import me.hubertus248.deployer.applications.model.entity.ApplicationName
import me.hubertus248.deployer.common.exception.NotFoundException
import me.hubertus248.deployer.security.IsAdmin
import me.hubertus248.deployer.applications.service.ApplicationService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

//todo write tests
@RestController
@RequestMapping("/api/applications")
class ApplicationController(
    private val applicationService: ApplicationService,
    private val applicationMapper: ApplicationMapper
) {

    @GetMapping
    fun listApplications(
        principal: Principal?,
        @PageableDefault(size = 10) pageable: Pageable
    ): Page<ApplicationDTO> {
        return if (principal == null) {
            applicationService.listPublicApplications(pageable)
        } else {
            applicationService.listApplications(pageable)
        }.map(applicationMapper::toApplicationDTO)
    }

    @IsAdmin
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{appId}")
    fun deleteApplication(@PathVariable appId: Long) {
        applicationService.deleteApplication(appId)
    }

    @IsAdmin
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun createApplication(@RequestBody @Validated createApplicationDTO: CreateApplicationDTO): ApplicationDTO {
        return applicationService.createApplication(
            ApplicationName(createApplicationDTO.name),
            createApplicationDTO.visibility,
            createApplicationDTO.manager
        ).let(applicationMapper::toApplicationDTO)
    }

    @GetMapping("/{appId}")
    fun getApplication(@PathVariable appId: Long, authentication: Authentication?): ApplicationDTO {
        return applicationService.findApplication(appId, authentication?.isAuthenticated ?: false)
            ?.let(applicationMapper::toApplicationDTO) ?: throw NotFoundException()
    }

}