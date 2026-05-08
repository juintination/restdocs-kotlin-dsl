package com.example.restdocskotlindsl.product.dto.request

import com.example.restdocskotlindsl.product.domain.enums.Category
import jakarta.validation.constraints.*

data class CreateProductRequest(
    @field:NotBlank(message = "상품명은 필수입니다")
    @field:Size(max = 100, message = "상품명은 100자를 초과할 수 없습니다")
    val name: String,

    @field:NotBlank(message = "상품 설명은 필수입니다")
    @field:Size(max = 1000, message = "상품 설명은 1000자를 초과할 수 없습니다")
    val description: String,

    @field:NotNull(message = "가격은 필수입니다")
    @field:Positive(message = "가격은 0보다 커야 합니다")
    val price: Long,

    @field:NotNull(message = "카테고리는 필수입니다")
    val category: Category,

    @field:NotNull(message = "재고는 필수입니다")
    @field:PositiveOrZero(message = "재고는 0 이상이어야 합니다")
    val stock: Int,
)
