package me.hubertus248.deployer.security

import org.springframework.security.access.annotation.Secured

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Secured("ROLE_deployer_admin")
annotation class IsAdmin