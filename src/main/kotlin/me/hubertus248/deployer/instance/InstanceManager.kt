package me.hubertus248.deployer.instance

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.Instance
import org.springframework.data.domain.Pageable
import javax.persistence.Column
import javax.persistence.Embeddable

abstract class InstanceManager {

    abstract fun getFriendlyName(): String

    abstract fun getUniqueName(): InstanceManagerName

    abstract fun registerApplication(application: Application)

    abstract fun listInstances(appId: Long, pageable: Pageable): List<Instance>

    abstract fun getAvailableFeatures(): Set<InstanceManagerFeature>

    abstract fun getOpenUrl(instance: Instance): String

    fun supportsFeature(feature: InstanceManagerFeature): Boolean = getAvailableFeatures().contains(feature)

    fun getCustomApplicationInfoFragment(): String {
        throw NotImplementedError()
    }
}

//TODO refactor like FileKey
@Embeddable
data class InstanceManagerName(
        @Column(length = 512)
        var managerName: String
) {
    init {
        require(managerName.isNotBlank())
    }
}

enum class InstanceManagerFeature {
    CUSTOM_APPLICATION_INFO,
    POSSIBLE_INSTANCE_LIST
}