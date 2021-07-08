package me.hubertus248.deployer.instance.docker.data

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class RegistryUsername(
    @Column(length = 128, updatable = true, unique = false, nullable = false, name = "registryUsername")
    val value: String
) {
    init {
        require(value.isNotBlank())
    }
}