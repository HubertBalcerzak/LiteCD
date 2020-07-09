package me.hubertus248.deployer.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ModelAttribute

@ControllerAdvice
class GlobalModelAdvice{

    @Value("\${deployer.domain}")
    private val domain: String =""

    @ModelAttribute("domain")
    fun populateDomain(): String = domain
}