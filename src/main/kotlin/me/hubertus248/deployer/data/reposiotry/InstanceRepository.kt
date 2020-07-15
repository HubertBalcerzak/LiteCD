package me.hubertus248.deployer.data.reposiotry

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.data.entity.InstanceKey
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InstanceRepository<T: Instance>: JpaRepository<T, Long> {

    fun findFirstByKeyAndApplication(instanceKey: InstanceKey,
                                     application: Application): T?

    fun findAllByApplication_Id(appId: Long, pageable: Pageable): List<T>

}