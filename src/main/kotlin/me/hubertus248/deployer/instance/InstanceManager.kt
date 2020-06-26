package me.hubertus248.deployer.instance

import me.hubertus248.deployer.data.entity.Application
import javax.persistence.Column
import javax.persistence.Embeddable

interface InstanceManager {

    fun getFriendlyName(): String

    fun getUniqueName(): InstanceManagerName

    fun registerApplication(application: Application)
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