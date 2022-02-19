package me.hubertus248.deployer.applications.exception

import me.hubertus248.deployer.common.exception.ApplicationException
import org.springframework.http.HttpStatus

/**
 * Thrown when InstanceManager is unable to process given application. It might indicate, that application data was tampered with, or it was created with a newer LiteCD version.
 */
class ApplicationCorruptedException(message: String) : ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, message)