package me.hubertus248.deployer.security

import com.nimbusds.jose.shaded.json.JSONArray
import com.nimbusds.jose.shaded.json.JSONObject
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component

@Component
class CustomJwtGrantedAuthoritiesConverter(
    private val securityProperties: SecurityProperties
) : Converter<Jwt, Collection<GrantedAuthority>> {

    private val converter = JwtGrantedAuthoritiesConverter()

    override fun convert(source: Jwt): List<GrantedAuthority>? {
        val authorities: List<GrantedAuthority> = (getRealmRoles(source) + getClientRoles(source))
            .map { role -> SimpleGrantedAuthority(ROLE_PREFIX + role) }
        return authorities + converter.convert(source).orEmpty()
    }

    private fun getRealmRoles(jwt: Jwt): List<String> = if (securityProperties.includeRealmRoles) {
        jwt.getClaimAsMap(REALM_ACCESS_CLAIM)
            ?.let { it[ROLES_CLAIM] as JSONArray? }
            ?.map { it as String } ?: emptyList()
    } else {
        emptyList()
    }

    private fun getClientRoles(jwt: Jwt): List<String> = if (securityProperties.includeClientRoles) {
        jwt.getClaimAsMap(RESOURCE_ACCESS_CLAIM)
            ?.let { it[securityProperties.clientName] as JSONObject? }
            ?.let { it[ROLES_CLAIM] as JSONArray? }
            ?.map { it as String } ?: emptyList()
    } else {
        emptyList()
    }

    companion object {
        const val REALM_ACCESS_CLAIM = "realm_access"
        const val ROLES_CLAIM = "roles"
        const val RESOURCE_ACCESS_CLAIM = "resource_access"
        const val ROLE_PREFIX = "ROLE_"
    }
}