package me.hubertus248.deployer.service

import me.hubertus248.deployer.data.dto.EnvironmentDTO
import me.hubertus248.deployer.data.entity.*
import me.hubertus248.deployer.data.reposiotry.ApplicationWithEnvironmentRepository
import me.hubertus248.deployer.data.reposiotry.EnvironmentVariableRepository
import me.hubertus248.deployer.data.reposiotry.InstanceWithEnvironmentRepository
import me.hubertus248.deployer.exception.NotFoundException
import me.hubertus248.deployer.instance.spring.application.SpringApplication
import me.hubertus248.deployer.instance.spring.instance.SpringInstance
import org.springframework.stereotype.Service
import javax.transaction.Transactional

interface EnvironmentService {

    fun updateApplicationEnvironment(appId: Long, environment: EnvironmentDTO)

    fun updateInstanceEnvironment(instanceId: Long, environment: EnvironmentDTO)

    fun setDefaultInstanceEnvironment(instance: InstanceWithEnvironment)

    fun getEnvironment(application: ApplicationWithEnvironment): Map<String, String>

    fun getEnvironment(instance: InstanceWithEnvironment, keywords: List<Keyword>): Map<String, String>

    fun getRawEnvironment(instance: InstanceWithEnvironment): Map<String, String>

    fun deleteInstanceEnvironment(instance: InstanceWithEnvironment)

    fun deleteDefaultEnvironment(application: ApplicationWithEnvironment)
}

@Service
class EnvironmentServiceImpl(
    private val applicationRepository: ApplicationWithEnvironmentRepository,
    private val instanceRepository: InstanceWithEnvironmentRepository,
    private val environmentVariableRepository: EnvironmentVariableRepository
) : EnvironmentService {

    @Transactional
    override fun updateApplicationEnvironment(appId: Long, environment: EnvironmentDTO) {
        val application = applicationRepository.findFirstById(appId) ?: throw NotFoundException()

        application.defaultEnvironment.forEach { environmentVariableRepository.delete(it) }
        application.defaultEnvironment.clear()

        environment.variables.map {
            EnvironmentVariable(
                0,
                EnvironmentVariableName(it.name),
                EnvironmentVariableValue(it.value)
            )
        }
            .forEach {
                environmentVariableRepository.save(it)
                application.defaultEnvironment.add(it)
            }
        applicationRepository.save(application)
    }

    @Transactional
    override fun updateInstanceEnvironment(instanceId: Long, environment: EnvironmentDTO) {
        val instance = instanceRepository.findFirstById(instanceId)
            ?: throw NotFoundException()

        instance.environment.forEach { environmentVariableRepository.delete(it) }
        instance.environment.clear()

        environment.variables.map {
            EnvironmentVariable(
                0,
                EnvironmentVariableName(it.name),
                EnvironmentVariableValue(it.value)
            )
        }
            .forEach {
                environmentVariableRepository.save(it)
                instance.environment.add(it)
            }
        instanceRepository.save(instance)
    }

    @Transactional
    override fun setDefaultInstanceEnvironment(instance: InstanceWithEnvironment) {
        val application = instance.application as ApplicationWithEnvironment
        instance.environment.forEach { environmentVariableRepository.delete(it) }
        instance.environment.clear()

        application.defaultEnvironment
            .map { EnvironmentVariable(0, it.name, EnvironmentVariableValue(it.value.value)) }
            .forEach {
                environmentVariableRepository.save(it)
                instance.environment.add(it)
            }
        instanceRepository.save(instance)
    }

    override fun getEnvironment(application: ApplicationWithEnvironment): Map<String, String> {
        return mapOf(*application.defaultEnvironment.map { Pair(it.name.value, it.value.value) }.toTypedArray())
    }

    override fun getEnvironment(instance: InstanceWithEnvironment, keywords: List<Keyword>): Map<String, String> {
        return mapOf(*instance.environment.map { Pair(it.name.value, replaceKeywords(it.value.value, keywords)) }
            .toTypedArray())
    }

    override fun getRawEnvironment(instance: InstanceWithEnvironment): Map<String, String> {
        return mapOf(*instance.environment.map { Pair(it.name.value, it.value.value) }.toTypedArray())
    }

    @Transactional
    override fun deleteInstanceEnvironment(instance: InstanceWithEnvironment) {
        instance.environment.forEach { environmentVariableRepository.delete(it) }
        instance.environment.clear()
    }

    @Transactional
    override fun deleteDefaultEnvironment(application: ApplicationWithEnvironment) {
        application.defaultEnvironment.forEach { environmentVariableRepository.delete(it) }
        application.defaultEnvironment.clear()
    }

    private fun replaceKeywords(input: String, keywords: List<Keyword>): String =
        keywords.foldRight(input) { keyword, acc -> acc.replace(keyword.keyword, keyword.value) }

}

data class Keyword(val keyword: String, val value: String)