package me.hubertus248.deployer.instance.docker.repository

import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.instance.docker.data.AvailableDockerInstance
import me.hubertus248.deployer.instance.docker.data.DockerApplication
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface AvailableDockerInstanceRepository:JpaRepository<AvailableDockerInstance, Long> {
    fun findFirstByApplicationAndKey(application: DockerApplication, instanceKey: InstanceKey): AvailableDockerInstance?
    fun findFirstByApplication_IdAndKey(applicationId: Long, instanceKey: InstanceKey): AvailableDockerInstance?
    fun findAllByApplication_Id(applicationId: Long, pageable: Pageable): List<AvailableDockerInstance>
    fun findAllByApplication(application: DockerApplication): List<AvailableDockerInstance>
}