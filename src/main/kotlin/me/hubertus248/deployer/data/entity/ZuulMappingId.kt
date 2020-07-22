package me.hubertus248.deployer.data.entity

import java.util.*
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class ZuulMappingId(
        @Column(name = "zuulMapping")
        val value: UUID
)