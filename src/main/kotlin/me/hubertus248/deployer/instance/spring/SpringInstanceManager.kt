package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.ApplicationName
import me.hubertus248.deployer.data.entity.Instance
import me.hubertus248.deployer.data.entity.Visibility
import me.hubertus248.deployer.instance.InstanceManager
import me.hubertus248.deployer.instance.InstanceManagerFeature
import me.hubertus248.deployer.instance.InstanceManagerName
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

val INSTANCE_MANAGER_SPRING_NAME = InstanceManagerName("INSTANCE_MANAGER_CORE_SPRING")


@Component
class SpringInstanceManager(
        private val springApplicationRepository: SpringApplicationRepository,
        private val springInstanceRepository: SpringInstanceRepository
) : InstanceManager() {
    override fun getFriendlyName(): String = "Spring Application"

    override fun getUniqueName(): InstanceManagerName = INSTANCE_MANAGER_SPRING_NAME

    override fun registerApplication(name: ApplicationName, visibility: Visibility): Application {
        val springApplication = SpringApplication(name, visibility)
        springApplicationRepository.save(springApplication)
        return springApplication
    }

    override fun listInstances(appId: Long, pageable: Pageable): List<Instance> {
        return springInstanceRepository.findAllById(appId, pageable)//TODO fix query
    }

    override fun getAvailableFeatures(): Set<InstanceManagerFeature> = emptySet()

    override fun getOpenUrl(instance: Instance): String = "/open/spring/${instance.id}"
}