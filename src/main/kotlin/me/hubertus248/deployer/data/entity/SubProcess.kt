package me.hubertus248.deployer.data.entity

import javax.persistence.*

@Entity
class SubProcess(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val processId: Long
)


enum class SubProcessStatus{
        RUNNING,
        DEAD
}