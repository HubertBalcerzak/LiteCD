package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.entity.Port
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

interface PortProviderService {
    fun getPort(): Port
    fun freePort(port: Port)
}

@Service
class PortProviderServiceImpl(
        @Value("\${deployer.allowed-port-range}")
        private val portRange: String
) : PortProviderService {

    private var minPort: Int = 0
    private var maxPort: Int = 0
    private val takenPorts: MutableMap<Int, Port> = mutableMapOf()
    private var nextPort: Int = 0
    private var numberOfTotalPorts: Int = 0

    init {
        val splitRange = portRange.split("-")
        if (splitRange.size != 2) throw IllegalArgumentException("Incorrect port range format.")
        minPort = splitRange[0].toInt()
        maxPort = splitRange[1].toInt()
        nextPort = minPort
        numberOfTotalPorts = maxPort - minPort
    }

    override fun getPort(): Port {
        synchronized(takenPorts) {
            if (numberOfTotalPorts <= takenPorts.size) throw IllegalStateException("There are no available ports")
            while (takenPorts.containsKey(nextPort)) incrementNextPort()
            return Port(nextPort).also {
                takenPorts[nextPort] = it
                incrementNextPort()
            }
        }
    }

    private fun incrementNextPort() {
        nextPort = (nextPort - minPort + 1) % maxPort + minPort
    }

    @Synchronized
    override fun freePort(port: Port) {
        synchronized(takenPorts) {
            if (takenPorts.containsKey(port.value)) takenPorts.remove(port.value)
        }
    }

}