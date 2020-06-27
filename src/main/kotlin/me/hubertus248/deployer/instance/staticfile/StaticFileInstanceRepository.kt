package me.hubertus248.deployer.instance.staticfile

import me.hubertus248.deployer.data.entity.InstanceKey
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface StaticFileInstanceRepository : JpaRepository<StaticFileInstance, Long> {

    fun findFirstByKeyAndStaticFileApplication(instanceKey: InstanceKey,
                                               staticFileApplication: StaticFileApplication): StaticFileInstance?

    @Query("""
        from StaticFileInstance i where i.staticFileApplication.application.id = ?1
    """)
    fun findAllByApplicationId(appId: Long, pageable: Pageable): List<StaticFileInstance>

    fun findFirstById(id: Long): StaticFileInstance?
}