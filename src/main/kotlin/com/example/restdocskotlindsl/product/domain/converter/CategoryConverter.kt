package com.example.restdocskotlindsl.product.domain.converter

import com.example.restdocskotlindsl.common.converter.BaseEnumConverter
import com.example.restdocskotlindsl.product.domain.enums.Category
import jakarta.persistence.Converter

@Converter(autoApply = true)
class CategoryConverter : BaseEnumConverter<Category>(Category::class.java)
