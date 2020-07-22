package me.hubertus248.deployer.data.entity

import me.hubertus248.deployer.util.Util
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class DomainLabel(
        @Column(length = 128, unique = true, name = "domainLabel")
        val value: String
) {
    init {
        require(value.isNotBlank())
        require(value.all { it.isLetterOrDigit() && if (it.isLetter()) it.isLowerCase() else true })
    }

    companion object {
        fun randomLabel(length: Int = 8): DomainLabel = DomainLabel(Util.secureReadableRandomString(length).toLowerCase())
    }
}