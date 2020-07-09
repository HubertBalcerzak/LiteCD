package me.hubertus248.deployer.data.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class Secret(
        @Column(length = 255, updatable = true, unique = false, nullable = false, name = "secret")
        val value: String
) {
    init {
        require(value.isNotBlank())
    }
}