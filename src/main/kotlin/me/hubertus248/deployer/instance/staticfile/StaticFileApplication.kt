package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.data.entity.Application
import javax.persistence.*

@Entity
class StaticFileApplication(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private val id: Long,

        @OneToOne
        private val application: Application,

        @Embedded
        private val secret: Secret
)

@Embeddable
data class Secret(
        @Column(length = 255, updatable = true, unique = false, nullable = false)
        var secret: String
)