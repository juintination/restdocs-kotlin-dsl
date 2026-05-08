package com.example.restdocskotlindsl.common.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String?,
) {
    companion object {
        fun <T> success(
            data: T,
        ) = ApiResponse(
            success = true,
            data = data,
            message = null,
        )

        fun <T> success(
            data: T,
            message: String,
        ) = ApiResponse(
            success = true,
            data = data,
            message = message,
        )

        fun <T> error(
            data: T,
            message: String,
        ) = ApiResponse(
            success = false,
            data = data,
            message = message,
        )

        fun error(
            message: String,
        ) = ApiResponse(
            success = false,
            data = null,
            message = message,
        )
    }
}
