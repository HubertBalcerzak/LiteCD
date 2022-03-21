package me.hubertus248.deployer.security

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

@EnableWebSecurity
class SecurityConfiguration(
    private val customJwtGrantedAuthoritiesConverter: CustomJwtGrantedAuthoritiesConverter
) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests {
            it.anyRequest()
                .permitAll()
        }.oauth2ResourceServer { oauth ->
            oauth.jwt { jwt ->
                jwt.jwtAuthenticationConverter(JwtAuthenticationConverter()
                    .apply { setJwtGrantedAuthoritiesConverter(customJwtGrantedAuthoritiesConverter) })
            }
        }
            .csrf().disable()
    }
}