package me.hubertus248.deployer.instance.spring

import me.hubertus248.deployer.data.dto.AvailableInstance
import me.hubertus248.deployer.data.entity.*
import me.hubertus248.deployer.instance.InstanceManager
import me.hubertus248.deployer.instance.InstanceManagerFeature
import me.hubertus248.deployer.instance.InstanceManagerName
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import me.hubertus248.deployer.instance.spring.application.SpringApplicationRepository
import me.hubertus248.deployer.instance.spring.instance.AvailableSpringInstanceService
import me.hubertus248.deployer.instance.spring.instance.SpringInstanceRepository
import me.hubertus248.deployer.util.Util
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component

val INSTANCE_MANAGER_SPRING_NAME = InstanceManagerName("INSTANCE_MANAGER_CORE_SPRING")


@Component
class SpringInstanceManager(
        private val springApplicationRepository: SpringApplicationRepository,
        private val springInstanceRepository: SpringInstanceRepository,
        private val availableSpringInstanceService: AvailableSpringInstanceService
) : InstanceManager() {
    private val util = Util()

    override fun getFriendlyName(): String = "Spring Application"

    override fun getUniqueName(): InstanceManagerName = INSTANCE_MANAGER_SPRING_NAME

    override fun registerApplication(name: ApplicationName, visibility: Visibility): Application {
        val springApplication = SpringApplication(Secret(util.secureAlphanumericRandomString(32)), name, visibility)
        springApplicationRepository.save(springApplication)
        return springApplication
    }

    override fun listInstances(appId: Long, pageable: Pageable): List<Instance> {
        return springInstanceRepository.findAllByApplication_Id(appId, pageable)
    }

    override fun getAvailableFeatures(): Set<InstanceManagerFeature> = setOf(
            InstanceManagerFeature.CUSTOM_APPLICATION_INFO,
            InstanceManagerFeature.POSSIBLE_INSTANCE_LIST)

    override fun getOpenUrl(instance: Instance): String = "/open/spring/${instance.id}"

    override fun getCustomApplicationInfoFragment(): String = "application/spring/springApplicationInfo.html"

    override fun getPossibleInstanceList(appId: Long, pageable: Pageable): List<AvailableInstance> =
            availableSpringInstanceService.listArtifacts(appId, pageable).map { AvailableInstance(it.key, it.lastUpdate) }
}