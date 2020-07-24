package me.hubertus248.deployer.data.reposiotry

import me.hubertus248.deployer.data.entity.EnvironmentVariable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EnvironmentVariableRepository: JpaRepository<EnvironmentVariable, Long> {

}