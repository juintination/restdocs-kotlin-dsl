package com.example.restdocskotlindsl.common.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
abstract class BaseEnumConverter<T : Enum<T>>(
    private val enumType: Class<T>,
) : AttributeConverter<T, String> {

    override fun convertToDatabaseColumn(
        attribute: T?,
    ): String? = attribute?.name

    override fun convertToEntityAttribute(
        dbData: String?,
    ): T? {
        if (dbData == null) return null
        return enumType.enumConstants.find { it.name == dbData }
    }
}
