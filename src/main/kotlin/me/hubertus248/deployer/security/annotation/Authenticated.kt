package me.hubertus248.deployer.security.annotation

import org.springframework.security.access.prepost.PreAuthorize

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('SCOPE_' + @oauthConfig.oauthScope)")
annotation class Authenticated