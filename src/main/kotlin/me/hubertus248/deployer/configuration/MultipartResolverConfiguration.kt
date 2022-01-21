package me.hubertus248.deployer.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.multipart.commons.CommonsMultipartResolver

@Configuration
class MultipartResolverConfiguration(val deployerProperties: DeployerProperties) {

    @Bean(name = ["multipartResolver"])
    fun multipartResolver(): CommonsMultipartResolver? {
        val multipartResolver = CommonsMultipartResolver()
        multipartResolver.setMaxUploadSize(deployerProperties.maxFileSize.toBytes())
        return multipartResolver
    }
}