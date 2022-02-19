package me.hubertus248.deployer.common.exception

import org.springframework.http.HttpStatus

class NotFoundException(message: String = "Not found.") : ApplicationException(HttpStatus.NOT_FOUND, message)

class BadRequestException(message: String = "Bad request.") : ApplicationException(HttpStatus.BAD_REQUEST, message)

class UnauthorizedException(message: String = "Unauthorized.") : ApplicationException(HttpStatus.UNAUTHORIZED, message)

class AccessDeniedException(message: String = "Access denied.") : ApplicationException(HttpStatus.FORBIDDEN, message)