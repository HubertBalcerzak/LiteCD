package me.hubertus248.deployer.instance

import me.hubertus248.deployer.data.entity.Application
import me.hubertus248.deployer.data.entity.Instance
import org.springframework.data.domain.Pageable
import javax.persistence.Column
import javax.persistence.Embeddable

interface InstanceManager {

    fun getFriendlyName(): String

    fun getUniqueName(): InstanceManagerName

    fun registerApplication(application: Application)

    fun listInstances(appId: Long, pageable: Pageable): List<Instance>

    fun getAvailableFeatures(): Set<InstanceManagerFeature>
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