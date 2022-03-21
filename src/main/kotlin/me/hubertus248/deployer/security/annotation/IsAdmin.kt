package me.hubertus248.deployer.security.annotation

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Authenticated
@PreAuthorize("hasRole(@oauthConfig.adminRole)")
annotation class IsAdmin