package me.hubertus248.deployer.controller

import me.hubertus248.deployer.configuration.LitecdProperties
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class GlobalModelAdvice(
    private val litecdProperties: LitecdProperties
){

    @ModelAttribute("domain")
    fun populateDomain(): String = litecdProperties.domain
}