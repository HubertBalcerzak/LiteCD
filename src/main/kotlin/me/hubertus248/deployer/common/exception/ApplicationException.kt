package me.hubertus248.deployer.common.exception

import org.springframework.http.HttpStatus

open class ApplicationException(
    val status: HttpStatus,
    val errorMessage: String,
    val details: List<String> = emptyList()
) : RuntimeException()