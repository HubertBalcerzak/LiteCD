package me.hubertus248.deployer.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.multipart.commons.CommonsMultipartResolver

@Configuration
class MultipartResolverConfiguration() {
    @Value("\${deployer.manager.staticfile.max-file-size}")
    private val maxFileSize: Long = 0

    @Bean(name = ["multipartResolver"])
    fun multipartResolver(): CommonsMultipartResolver? {
        val multipartResolver = CommonsMultipartResolver()
        multipartResolver.setMaxUploadSize(1000 * maxFileSize)
        return multipartResolver
    }
}