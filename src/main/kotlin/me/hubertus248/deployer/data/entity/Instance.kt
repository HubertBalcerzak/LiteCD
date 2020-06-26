package me.hubertus248.deployer.data.entity

        import javax.persistence.*

@Entity
class Instance(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        val application: Application

)