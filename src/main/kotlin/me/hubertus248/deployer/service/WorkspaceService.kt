package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.entity.Workspace
import me.hubertus248.deployer.data.reposiotry.WorkspaceRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.lang.IllegalStateException
import java.nio.file.Path
import javax.annotation.PostConstruct
import javax.transaction.Transactional


interface WorkspaceService {

    fun createWorkspace(): Workspace

    fun deleteWorkspace(workspace: Workspace)

    fun getWorkspaceRoot(workspace: Workspace): Path
}

@Service
class WorkspaceServiceImpl(
        private val workspaceRepository: WorkspaceRepository
) : WorkspaceService {

    @Value("\${deployer.filesystem-storage-path}")
    private lateinit var dataRoot: String
    private lateinit var workspacesRoot: Path
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun createWorkspace(): Workspace {
        val newWorkspace = Workspace(0)
        workspaceRepository.save(newWorkspace)

        val newWorkspaceRoot = getPathFromId(newWorkspace.id).toFile()

        if (newWorkspaceRoot.exists())
            throw IllegalStateException("Workspace folder (path ${newWorkspaceRoot.path}) already exists.")

        newWorkspaceRoot.mkdirs()
        logger.info("New workspace (id ${newWorkspace.id}) created")
        return newWorkspace
    }

    @Transactional
    override fun deleteWorkspace(workspace: Workspace) {
        val workspaceRoot = getPathFromId(workspace.id).toFile()
        if (!workspaceRoot.exists()) {
            logger.warn("Could not delete workspace ${workspace.id} - Root doesn't exist. Was it deleted externally?")
            return
        }
        if (!workspaceRoot.deleteRecursively()) {
            logger.error("Could not delete workspace id ${workspace.id} at ${workspaceRoot.path}")
            throw IllegalStateException("Could not delete workspace")
        }

        workspaceRepository.delete(workspace)
        logger.info("Deleted workspace $workspace.id")
    }

    override fun getWorkspaceRoot(workspace: Workspace): Path {
        val path = getPathFromId(workspace.id)

        if (!path.toFile().exists()) throw IllegalStateException("Workspace with if ${workspace.id} doesn't exist.")

        return path
    }


    @PostConstruct
    private fun initializeWorkspaces() {
        logger.info("Initializing workspaces")
        workspacesRoot = Path.of(dataRoot, "workspaces")
        val workspacesRootFile = workspacesRoot.toFile()

        if (workspacesRootFile.exists()) {
            if (workspacesRootFile.isDirectory) return
            else throw IllegalStateException("Workspace root \"${workspacesRootFile.path}\" already exists and is not a directory")
        } else {
            workspacesRootFile.mkdirs()
        }
    }

    private fun getPathFromId(id: Long): Path = Path.of(workspacesRoot.toString(), id.toString())

}