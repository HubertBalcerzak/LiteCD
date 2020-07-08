package me.hubertus248.deployer.instance.spring.instance

import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AvailableSpringInstanceRepository : JpaRepository<AvailableSpringInstance, Long> {

    fun findFirstByApplicationAndKey(application: SpringApplication, instanceKey: InstanceKey): AvailableSpringInstance?
    fun findFirstByApplication_IdAndKey(applicationId: Long, instanceKey: InstanceKey): AvailableSpringInstance?
    fun findAllByApplication_Id(applicationId: Long, pageable: Pageable): List<AvailableSpringInstance>
    fun findAllByApplication(application: SpringApplication, pageable: Pageable): List<AvailableSpringInstance>
}