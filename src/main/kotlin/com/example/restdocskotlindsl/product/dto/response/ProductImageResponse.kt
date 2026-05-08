package com.example.restdocskotlindsl.product.dto.response

import com.example.restdocskotlindsl.product.domain.Product

data class ProductImageResponse(
    val id: Long,
    val imageUrl: String,
) {
    companion object {
        fun from(
            product: Product,
        ) = ProductImageResponse(
            id = product.id!!,
            imageUrl = product.imageUrl!!,
        )
    }
}
