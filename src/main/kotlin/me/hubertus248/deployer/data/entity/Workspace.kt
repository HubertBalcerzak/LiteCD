package me.hubertus248.deployer.data.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Workspace(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long
)