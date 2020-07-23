package me.hubertus248.deployer.data.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class Port(
        @Column(name = "port")
        val value: Int
) {
    init {

    }
}