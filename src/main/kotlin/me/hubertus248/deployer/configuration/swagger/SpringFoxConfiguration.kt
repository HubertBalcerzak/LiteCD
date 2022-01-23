package me.hubertus248.deployer.configuration.swagger

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket

@Configuration
class SpringFoxConfiguration {

@Bean
fun api(): Docket = Docket(DocumentationType.SWAGGER_2)
    .select()
    .apis { true }
    .paths(PathSelectors.ant("/api/**"))
    .build()
}