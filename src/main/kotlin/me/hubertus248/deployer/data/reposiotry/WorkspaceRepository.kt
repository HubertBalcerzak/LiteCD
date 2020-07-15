package me.hubertus248.deployer.data.reposiotry

import me.hubertus248.deployer.data.entity.Workspace
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WorkspaceRepository : JpaRepository<Workspace, Long> {

}