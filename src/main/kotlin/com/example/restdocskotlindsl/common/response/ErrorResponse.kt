package com.example.restdocskotlindsl.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val timestamp: String,
    val path: String,
    val method: String,
    val code: String,
    val errors: List<FieldErrorDetail>?,
) {
    data class FieldErrorDetail(
        val field: String,
        val code: String?,
        val message: String?,
    )

    companion object {
        fun of(
            path: String,
            method: String,
            code: String,
            errors: List<FieldErrorDetail>? = null,
        ) = ErrorResponse(
            timestamp = Instant.now().toString(),
            path = path,
            method = method,
            code = code,
            errors = errors,
        )
    }
}
