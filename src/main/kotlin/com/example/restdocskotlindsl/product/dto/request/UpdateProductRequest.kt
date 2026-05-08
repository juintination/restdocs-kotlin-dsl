package com.example.restdocskotlindsl.product.dto.request

import com.example.restdocskotlindsl.product.domain.enums.Category
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

data class UpdateProductRequest(
    @field:Size(min = 1, max = 100, message = "상품명은 1자 이상 100자 이하여야 합니다")
    val name: String? = null,

    @field:Size(min = 1, max = 1000, message = "상품 설명은 1자 이상 1000자 이하여야 합니다")
    val description: String? = null,

    @field:Positive(message = "가격은 0보다 커야 합니다")
    val price: Long? = null,

    val category: Category? = null,

    @field:PositiveOrZero(message = "재고는 0 이상이어야 합니다")
    val stock: Int? = null,
)
