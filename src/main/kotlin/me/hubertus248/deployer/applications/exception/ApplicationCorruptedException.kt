package me.hubertus248.deployer.applications.exception

import me.hubertus248.deployer.common.exception.ApplicationException
import org.springframework.http.HttpStatus

class ApplicationCorruptedException(message: String) : ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, message)