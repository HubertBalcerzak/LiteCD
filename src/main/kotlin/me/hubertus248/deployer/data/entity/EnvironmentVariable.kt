package me.hubertus248.deployer.data.entity

import javax.persistence.*

@Entity
class EnvironmentVariable(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @Embedded
        val name: EnvironmentVariableName,

        @Embedded
        val value: EnvironmentVariableValue
)

@Embeddable
data class EnvironmentVariableName(
        @Column(name = "environmentVariableName")
        val value: String
) {
    init {
        require(value.all { it.isLetterOrDigit() || it == '_' })
    }
}

@Embeddable
data class EnvironmentVariableValue(
        @Column(name = "environmentVariableValue")
        val value: String
) {
    init {

    }
}