package com.example.restdocskotlindsl.product.domain

import com.example.restdocskotlindsl.product.domain.enums.Category
import io.hypersistence.utils.hibernate.id.Tsid
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "products")
class Product private constructor(
    @Id
    @Tsid
    @Column(name = "id", nullable = false, updatable = false)
    val id: Long? = null,

    @Column(name = "name", nullable = false, length = 100)
    var name: String,

    @Column(name = "description", nullable = false, length = 1000)
    var description: String,

    @Column(name = "price", nullable = false)
    var price: Long,

    @Column(name = "category", nullable = false, length = 20)
    var category: Category,

    @Column(name = "stock", nullable = false)
    var stock: Int,

    @Column(name = "image_url", length = 500)
    var imageUrl: String? = null,
) {
    companion object {
        fun create(
            name: String,
            description: String,
            price: Long,
            category: Category,
            stock: Int,
        ) = Product(
            name = name,
            description = description,
            price = price,
            category = category,
            stock = stock,
        )
    }

    fun update(
        name: String? = null,
        description: String? = null,
        price: Long? = null,
        category: Category? = null,
        stock: Int? = null,
    ) {
        name?.let { this.name = it }
        description?.let { this.description = it }
        price?.let { this.price = it }
        category?.let { this.category = it }
        stock?.let { this.stock = it }
    }

    fun updateImageUrl(
        imageUrl: String,
    ) {
        this.imageUrl = imageUrl
    }
}
