package me.hubertus248.deployer.data.entity

import javax.persistence.*

@Entity
class Application(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @Column
        val name: String
)