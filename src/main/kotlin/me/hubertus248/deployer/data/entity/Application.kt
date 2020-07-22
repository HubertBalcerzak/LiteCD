package me.hubertus248.deployer.data.entity

import me.hubertus248.deployer.exception.BadRequestException
import me.hubertus248.deployer.instance.InstanceManagerName
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class Application(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        open val id: Long,

        @Embedded
        open val name: ApplicationName,

        @Column(unique = false, updatable = true)
        open val visibility: Visibility,

        @Embedded
        open val manager: InstanceManagerName,

        @OneToMany(fetch = FetchType.LAZY)
        open val instances: Set<Instance> = emptySet(),

        @Column
        open val creationDateTime: LocalDateTime = LocalDateTime.now()
)

enum class Visibility {
    PUBLIC,
    RESTRICTED
}

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