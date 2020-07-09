package me.hubertus248.deployer.data.entity

import javax.persistence.*

@Entity
abstract class Instance(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        open val id: Long,

        @Embedded
        open val key: InstanceKey,

        @ManyToOne
        open val application: Application
)


@Embeddable
data class InstanceKey(
        @Access(AccessType.FIELD)
        @Column(name = "instanceKey", nullable = false, unique = false, length = 130)
        val value: String
) {
    init {
        require(value.isNotBlank())
        require(value.length in 3..128)
    }
}