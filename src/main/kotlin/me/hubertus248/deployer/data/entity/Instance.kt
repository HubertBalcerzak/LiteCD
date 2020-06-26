package me.hubertus248.deployer.data.entity

import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.Embeddable

interface Instance{
        fun getName(): String
}

@Embeddable
data class InstanceKey(
        @Access(AccessType.FIELD)
        @Column(name = "instanceKey", nullable = false, unique = false)
        val value: String
){
        init {
                require(value.isNotBlank())
                require(value.length in 3..128)
        }
}