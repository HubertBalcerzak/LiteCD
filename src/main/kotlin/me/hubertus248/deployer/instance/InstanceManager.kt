package me.hubertus248.deployer.instance

import me.hubertus248.deployer.data.dto.AvailableInstance
import me.hubertus248.deployer.data.entity.*
import org.aspectj.weaver.ast.Not
import org.springframework.data.domain.Pageable
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.Embeddable

abstract class InstanceManager {

    abstract fun getFriendlyName(): String

    abstract fun getUniqueName(): InstanceManagerName

    abstract fun registerApplication(name: ApplicationName, visibility: Visibility): Application

    abstract fun listInstances(appId: Long, pageable: Pageable): List<Instance>

    abstract fun getAvailableFeatures(): Set<InstanceManagerFeature>

    abstract fun getOpenUrl(instance: Instance): String

    abstract fun prepareForApplicationDeletion(appId: Long)

    //TODO redesign application specific features
    fun supportsFeature(feature: InstanceManagerFeature): Boolean = getAvailableFeatures().contains(feature)

    open fun getCustomApplicationInfoFragment(): String = throw NotImplementedError()

    open fun getPossibleInstanceList(appId: Long, pageable: Pageable): List<AvailableInstance> = emptyList()

    open fun createInstance(appId: Long, instanceKey: InstanceKey): Instance = throw NotImplementedError()

    open fun startInstance(instance: Instance) {
        throw NotImplementedError()
    }

    open fun stopInstance(appId: Long, instanceKey: InstanceKey) {
        throw NotImplementedError()
    }

    open fun deleteInstance(appId: Long, instanceKey: InstanceKey) {
        throw NotImplementedError()
    }

    open fun recreateInstance(appId: Long, instanceKey: InstanceKey) {
        throw NotImplementedError()
    }

    open fun configureApplicationUrl(appId: Long): String = throw NotImplementedError()

    open fun configureInstanceUrl(appId: Long, instanceId: Long): String = throw NotImplementedError()

    open fun deleteAvailableInstance(appId: Long, instanceKey: InstanceKey ){
        throw NotImplementedError()
    }
}

//TODO refactor like FileKey
@Embeddable
data class InstanceManagerName(
        @Access(AccessType.FIELD)
        @Column(length = 512, name = "instanceManagerName")
        val value: String
) {
    init {
        require(value.isNotBlank())
    }
}

enum class InstanceManagerFeature {
    /**
     * custom modal with application info
     */
    CUSTOM_APPLICATION_INFO,

    /**
     * instances created from list of available instances
     */
    POSSIBLE_INSTANCE_LIST,

    /**
     * instances support STOP/START commands
     */
    STOPPABLE_INSTANCES,

    /**
     * custom configure application page
     */
    CONFIGURABLE_APPLICATION,

    /**
     * custom instance details/configuration page
     */
    CONFIGURABLE_INSTANCES
}