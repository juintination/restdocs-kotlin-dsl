package com.example.restdocskotlindsl.product.dto.response

import com.example.restdocskotlindsl.product.domain.Product

data class ProductResponse(
    val id: Long,
    val name: String,
    val description: String,
    val price: Long,
    val category: String,
    val stock: Int,
    val imageUrl: String?,
) {
    companion object {
        fun from(
            product: Product,
        ) = ProductResponse(
            id = product.id!!,
            name = product.name,
            description = product.description,
            price = product.price,
            category = product.category.name,
            stock = product.stock,
            imageUrl = product.imageUrl,
        )
    }
}
