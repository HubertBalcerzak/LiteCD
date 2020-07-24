package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.entity.SubProcessStatus
import me.hubertus248.deployer.data.entity.SubProcess
import me.hubertus248.deployer.data.reposiotry.SubProcessRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.lang.IllegalArgumentException
import javax.annotation.PostConstruct
import javax.transaction.Transactional

interface SubProcessService {
    fun startProcess(workingDirectory: File, logFile: File, command: List<String>, environmentAdditions: Map<String, String>): SubProcess

    fun stopProcess(process: SubProcess)

    fun getProcessStatus(process: SubProcess): SubProcessStatus
}

@Service
class SubProcessServiceImpl(
        private val subProcessRepository: SubProcessRepository
) : SubProcessService {
    private val processes = mutableMapOf<Long, Process>()
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostConstruct
    private fun init() {
        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            processes.forEach {
                it.value.destroyForcibly()
            }
        }))
    }

    @Transactional
    override fun startProcess(workingDirectory: File, logFile: File, command: List<String>, environmentAdditions: Map<String, String>): SubProcess {
        if (!workingDirectory.exists() || !workingDirectory.isDirectory)
            throw IllegalArgumentException("Incorrect working directory ${workingDirectory.path}")

        if (logFile.isDirectory) throw IllegalArgumentException("Logfile ${logFile.path} is a directory")

        val processId = SubProcess(0)
        subProcessRepository.save(processId)
        logger.info("Starting new subprocess with id ${processId.processId}")
        val newProcessBuilder = ProcessBuilder()

        val environment = newProcessBuilder.environment()
        cleanEnv(environment)
        environment.putAll(environmentAdditions)

        val newProcess = newProcessBuilder
                .directory(workingDirectory)
                .command(command)
                .redirectErrorStream(true)
                .redirectOutput(logFile)
                .start()
        processes[processId.processId] = newProcess
        return processId
    }

    override fun stopProcess(process: SubProcess) {
        if (!processes.containsKey(process.processId)) return
        processes[process.processId]?.destroyForcibly()
        processes.remove(process.processId)
    }

    override fun getProcessStatus(process: SubProcess): SubProcessStatus {
        return if (processes[process.processId]?.isAlive == true) SubProcessStatus.RUNNING else SubProcessStatus.DEAD
    }

    private fun cleanEnv(env: MutableMap<String, String>) {
        val keysToDelete = env.keys.filter { it.startsWith("DEPLOYER_") }
        keysToDelete.forEach { env.remove(it) }
    }
}
