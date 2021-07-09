package me.hubertus248.deployer.instance.docker.data

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class ImageName(
    @Column(length = 255, updatable = true, unique = false, name = "imageName")
    val value: String
) {
    init {
        require(value.isNotBlank())
        require(value.length < 255)
    }
}