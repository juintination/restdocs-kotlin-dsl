package com.example.restdocskotlindsl.common.exception

import com.example.restdocskotlindsl.common.response.ApiResponse
import com.example.restdocskotlindsl.common.response.ErrorResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = KotlinLogging.logger {}

    /**
     * @Valid (RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        req: HttpServletRequest,
    ): ResponseEntity<ApiResponse<*>> {
        log.warn { "Validation failed: ${ex.message}" }

        val errors = ex.bindingResult.fieldErrors.map { err ->
            ErrorResponse.FieldErrorDetail(
                field = err.field,
                code = err.code,
                message = err.defaultMessage,
            )
        }

        return ResponseEntity
            .status(ErrorCode.INVALID_ARGUMENT.status)
            .body(
                ApiResponse.error(
                    data = ErrorResponse.of(
                        path = req.requestURI,
                        method = req.method,
                        code = ErrorCode.INVALID_ARGUMENT.name,
                        errors = errors,
                    ),
                    message = ErrorCode.INVALID_ARGUMENT.message,
                )
            )
    }

    /**
     * @Validated (QueryParam, PathVariable)
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(
        ex: ConstraintViolationException,
        req: HttpServletRequest,
    ): ResponseEntity<ApiResponse<*>> {
        log.warn { "Constraint violation: ${ex.message}" }

        val errors = ex.constraintViolations.map { v ->
            ErrorResponse.FieldErrorDetail(
                field = extractField(v.propertyPath.toString()),
                code = v.constraintDescriptor.annotation.annotationClass.simpleName,
                message = v.message,
            )
        }

        return ResponseEntity
            .status(ErrorCode.INVALID_ARGUMENT.status)
            .body(
                ApiResponse.error(
                    data = ErrorResponse.of(
                        path = req.requestURI,
                        method = req.method,
                        code = ErrorCode.INVALID_ARGUMENT.name,
                        errors = errors,
                    ),
                    message = ErrorCode.INVALID_ARGUMENT.message,
                )
            )
    }

    /**
     * JSON 파싱 오류
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(
        ex: HttpMessageNotReadableException,
        req: HttpServletRequest,
    ): ResponseEntity<ApiResponse<*>> {
        log.warn { "Malformed request body: ${ex.message}" }

        return ResponseEntity
            .status(ErrorCode.INVALID_REQUEST_BODY.status)
            .body(
                ApiResponse.error(
                    data = ErrorResponse.of(
                        path = req.requestURI,
                        method = req.method,
                        code = ErrorCode.INVALID_REQUEST_BODY.name,
                    ),
                    message = ErrorCode.INVALID_REQUEST_BODY.message,
                )
            )
    }

    /**
     * 비즈니스 예외
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(
        ex: BusinessException,
        req: HttpServletRequest,
    ): ResponseEntity<ApiResponse<*>> {
        val code = ex.errorCode

        log.info { "Business exception: code=${code.name}, message=${code.message}" }

        return ResponseEntity
            .status(code.status)
            .body(
                ApiResponse.error(
                    data = ErrorResponse.of(
                        path = req.requestURI,
                        method = req.method,
                        code = code.name,
                    ),
                    message = code.message,
                )
            )
    }

    /**
     * 예상하지 못한 예외
     */
    @ExceptionHandler(Exception::class)
    fun handleEtc(
        ex: Exception,
        req: HttpServletRequest,
    ): ResponseEntity<ApiResponse<*>> {
        if (ex is NoResourceFoundException || ex is NoHandlerFoundException) throw ex

        log.error(ex) { "Unhandled exception" }

        return ResponseEntity
            .status(ErrorCode.INTERNAL_ERROR.status)
            .body(
                ApiResponse.error(
                    data = ErrorResponse.of(
                        path = req.requestURI,
                        method = req.method,
                        code = ErrorCode.INTERNAL_ERROR.name,
                    ),
                    message = ErrorCode.INTERNAL_ERROR.message,
                )
            )
    }

    private fun extractField(
        path: String,
    ): String {
        val lastDot = path.lastIndexOf('.')
        return if (lastDot != -1) path.substring(lastDot + 1) else path
    }
}
