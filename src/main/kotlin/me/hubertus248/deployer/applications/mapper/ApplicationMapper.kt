package me.hubertus248.deployer.applications.mapper

import me.hubertus248.deployer.applications.model.dto.ApplicationDTO
import me.hubertus248.deployer.applications.model.entity.Application
import me.hubertus248.deployer.applications.model.entity.ApplicationName
import me.hubertus248.deployer.instancemanager.InstanceManagerName
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface ApplicationMapper {

    fun toApplicationDTO(application: Application): ApplicationDTO

    fun applicationNameToDTO(applicationName: ApplicationName): String = applicationName.value

    fun managerNameToDTO(managerName: InstanceManagerName) = managerName.value

}