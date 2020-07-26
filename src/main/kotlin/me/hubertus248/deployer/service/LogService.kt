package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.entity.Workspace
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Path

const val LOG_FILE_NAME = "log.txt"

interface LogService {
    fun getRecentLogs(workspace: Workspace): List<String>

    fun getLogFile(workspace: Workspace): File?
}

@Service
class LogServiceImpl(
        private val workspaceService: WorkspaceService
) : LogService {

    @Value("\${deployer.displayed-log-size}")
    private val maxLogSize: Long = 0

    override fun getRecentLogs(workspace: Workspace): List<String> {
        val file = getLogFile(workspace) ?: return emptyList()
        val fileSize = FileUtils.sizeOf(file)
        return if (fileSize > maxLogSize) {
            val inputStream = file.inputStream()
            IOUtils.skip(inputStream, fileSize - maxLogSize)
            IOUtils.readLines(inputStream)
        } else file.readLines()
    }

    override fun getLogFile(workspace: Workspace): File? {
        val workspaceRootPath = workspaceService.getWorkspaceRoot(workspace)

        val logfile = Path.of(workspaceRootPath.toString(), LOG_FILE_NAME).toFile()

        if (!logfile.exists()) return null
        if (logfile.isDirectory) throw IllegalStateException("Logfile is a directory (path \"${logfile.path}\")")
        return logfile
    }

}