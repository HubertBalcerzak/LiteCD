package me.hubertus248.deployer.applications.repository

import me.hubertus248.deployer.applications.model.entity.Application
import me.hubertus248.deployer.applications.model.entity.ApplicationName
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ApplicationRepository : JpaRepository<Application, Long> {

    @Query("""
        from Application a where a.visibility=me.hubertus248.deployer.applications.model.entity.Visibility.PUBLIC
    """)
    fun findAllPublic(pageable: Pageable): Page<Application>

    fun findFirstById(id: Long): Application?

    @Query("""
        from Application a where a.id=?1 and a.visibility=me.hubertus248.deployer.applications.model.entity.Visibility.PUBLIC
    """)
    fun findFirstPublicById(id: Long): Application?

    fun findFirstByName(name: ApplicationName): Application?
}