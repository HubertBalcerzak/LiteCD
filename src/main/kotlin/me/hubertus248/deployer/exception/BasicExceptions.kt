package me.hubertus248.deployer.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException() : RuntimeException()

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException() : RuntimeException()

@ResponseStatus(HttpStatus.FORBIDDEN)
class UnauthorizedException() : RuntimeException()