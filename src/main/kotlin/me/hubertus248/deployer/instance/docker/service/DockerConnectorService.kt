package me.hubertus248.deployer.instance.docker.service

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import com.github.dockerjava.transport.DockerHttpClient
import me.hubertus248.deployer.data.entity.InstanceKey
import me.hubertus248.deployer.data.entity.InstanceStatus
import me.hubertus248.deployer.instance.docker.data.DockerApplication
import me.hubertus248.deployer.instance.docker.data.DockerInstance
import org.springframework.stereotype.Service
import java.time.Duration


interface DockerConnectorService {

    fun startContainer(instance: DockerInstance)

    fun stopContainer(instance: DockerInstance)

    fun deleteImage(instanceKey: InstanceKey)

    fun getInstanceStatus(dockerInstance: DockerInstance): InstanceStatus
}

@Service
class DockerConnectorServiceImpl : DockerConnectorService {


    private fun getClient(application: DockerApplication): DockerClient {
        val config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .build()

        val httpClient: DockerHttpClient = ApacheDockerHttpClient.Builder()
            .dockerHost(config.dockerHost)
            .sslConfig(config.sslConfig)
            .maxConnections(100)
//            .connectionTimeout(Duration.ofSeconds(30))
//            .responseTimeout(Duration.ofSeconds(45))
            .build()
        val client = DockerClientImpl.getInstance(config, httpClient)
        client.pingCmd().exec()
//        val httpClient: DockerHttpClient = Builder()
//            .dockerHost(config.getDockerHost())
//            .sslConfig(config.getSSLConfig())
//            .maxConnections(100)
//            .connectionTimeout(Duration.ofSeconds(30))
//            .responseTimeout(Duration.ofSeconds(45))
//            .build()
        return client
    }

    override fun startContainer(instance: DockerInstance) {
        TODO("Not yet implemented")
    }

    override fun stopContainer(instance: DockerInstance) {
        TODO("Not yet implemented")
    }

    override fun deleteImage(instanceKey: InstanceKey) {
        TODO("Not yet implemented")
    }

    override fun getInstanceStatus(dockerInstance: DockerInstance): InstanceStatus {
        TODO("Not yet implemented")
    }
}