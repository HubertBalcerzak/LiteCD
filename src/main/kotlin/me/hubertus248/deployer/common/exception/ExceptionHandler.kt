package me.hubertus248.deployer.common.exception

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import me.hubertus248.deployer.common.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestCookieException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.multipart.support.MissingServletRequestPartException

@ControllerAdvice
class ExceptionHandler {

    private val log = getLogger()

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(applicationException: ApplicationException): ResponseEntity<ErrorDTO> {
        log.info("Responding with status ${applicationException.status}. Reason: ${applicationException.errorMessage}")
        return ResponseEntity(
            ErrorDTO(applicationException.errorMessage, applicationException.details),
            applicationException.status
        )
    }


    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(exception: HttpMessageNotReadableException): ResponseEntity<ErrorDTO> {
        val message = if (exception.cause is MissingKotlinParameterException) {
            "Bad request. Missing required parameter '${(exception.cause as MissingKotlinParameterException).parameter.name}'."
        } else "Bad request."

        return handleException(exception, message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(exception: MethodArgumentNotValidException): ResponseEntity<ErrorDTO> =
        handleException(
            exception,
            "Bad request. Missing or invalid parameter '${exception.parameter.parameterName}.'",
            HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleArgumentTypeMismatch(exception: MethodArgumentTypeMismatchException): ResponseEntity<ErrorDTO> =
        handleException(exception, "Bad request. Malformed parameter ${exception.parameter}", HttpStatus.BAD_REQUEST)

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleUnsupportedMethodException(exception: HttpRequestMethodNotSupportedException): ResponseEntity<ErrorDTO> =
        handleException(exception, "Method not allowed.", HttpStatus.METHOD_NOT_ALLOWED)

    @ExceptionHandler(MissingServletRequestPartException::class)
    fun handleMissingServletRequestException(exception: MissingServletRequestPartException): ResponseEntity<ErrorDTO> =
        handleException(exception, "Bad Request. Malformed multipart request.", HttpStatus.BAD_REQUEST)

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(exception: MissingServletRequestParameterException): ResponseEntity<ErrorDTO> =
        handleException(
            exception,
            "Bad Request. Missing required parameter '${exception.parameterName}'.",
            HttpStatus.BAD_REQUEST
        )


    @ExceptionHandler(MissingRequestCookieException::class)
    fun handleMissingCookieException(exception: MissingRequestCookieException): ResponseEntity<ErrorDTO> =
        handleException(
            exception,
            "Bad Request. Cookie \"${exception.cookieName}\" not present.",
            HttpStatus.BAD_REQUEST
        )


    @ExceptionHandler(MultipartException::class)
    fun handleMultipartException(exception: MultipartException): ResponseEntity<ErrorDTO> =
        handleException(exception, "Bad Request. Malformed multipart request.", HttpStatus.BAD_REQUEST)


    @ExceptionHandler(Exception::class)
    fun handleAll(exception: Exception): ResponseEntity<ErrorDTO> {
        log.error("Internal server error", exception)
        return ResponseEntity(ErrorDTO("Internal sever error."), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun handleException(exception: Exception, message: String, status: HttpStatus): ResponseEntity<ErrorDTO> {
        log.info("Exception during request handling.", exception)
        return ResponseEntity(ErrorDTO(message), status)
    }
}