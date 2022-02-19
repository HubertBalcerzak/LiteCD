package me.hubertus248.deployer.applications.model.entity

import me.hubertus248.deployer.common.exception.BadRequestException
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class ApplicationName(
    @Column(name = "name", length = 128, nullable = false, unique = true, updatable = false)
    val value: String
) {
    init {
        try {
            require(value.isNotBlank())
            require(value.all { it.isLetterOrDigit() || "_ -".contains(it) })
        } catch (e: IllegalArgumentException) {
            throw BadRequestException()
        }
    }
}