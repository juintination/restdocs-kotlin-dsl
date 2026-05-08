package com.example.restdocskotlindsl.common.exception

class BusinessException(
    val errorCode: ErrorCode,
) : RuntimeException(errorCode.message)
