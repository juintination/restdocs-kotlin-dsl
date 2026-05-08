package com.example.restdocskotlindsl.product.service

import com.example.restdocskotlindsl.common.exception.BusinessException
import com.example.restdocskotlindsl.common.exception.ErrorCode
import com.example.restdocskotlindsl.product.domain.Product
import com.example.restdocskotlindsl.product.domain.enums.Category
import com.example.restdocskotlindsl.product.dto.request.CreateProductRequest
import com.example.restdocskotlindsl.product.dto.request.UpdateProductRequest
import com.example.restdocskotlindsl.product.dto.response.ProductImageResponse
import com.example.restdocskotlindsl.product.dto.response.ProductResponse
import com.example.restdocskotlindsl.product.repository.ProductRepository
import io.hypersistence.tsid.TSID
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {

    @Transactional
    fun create(
        request: CreateProductRequest,
    ): ProductResponse {
        val product = productRepository.save(
            Product.create(
                name = request.name,
                description = request.description,
                price = request.price,
                category = request.category,
                stock = request.stock,
            )
        )
        return ProductResponse.from(product)
    }

    @Transactional(readOnly = true)
    fun findAll(
        category: Category? = null,
        minPrice: Long? = null,
        maxPrice: Long? = null,
    ): List<ProductResponse> = productRepository.findAll()
        .filter { category == null || it.category == category }
        .filter { minPrice == null || it.price >= minPrice }
        .filter { maxPrice == null || it.price <= maxPrice }
        .map { ProductResponse.from(it) }

    @Transactional(readOnly = true)
    fun findById(
        id: Long,
    ): ProductResponse {
        val product = productRepository.findByIdOrNull(id)
            ?: throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
        return ProductResponse.from(product)
    }

    @Transactional
    fun update(
        id: Long,
        request: UpdateProductRequest,
    ): ProductResponse {
        val product = productRepository.findByIdOrNull(id)
            ?: throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)

        product.update(
            name = request.name,
            description = request.description,
            price = request.price,
            category = request.category,
            stock = request.stock,
        )
        return ProductResponse.from(product)
    }

    @Transactional
    fun delete(
        id: Long,
    ) {
        if (!productRepository.existsById(id)) {
            throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
        }

        productRepository.deleteById(id)
    }

    @Transactional
    fun uploadImage(
        id: Long,
        file: MultipartFile,
    ): ProductImageResponse {
        val product = productRepository.findByIdOrNull(id)
            ?: throw BusinessException(ErrorCode.PRODUCT_NOT_FOUND)
        val extension = file.originalFilename
            ?.substringAfterLast('.', "")
            ?.takeIf { it.isNotEmpty() }
            ?.let { ".$it" } ?: ""
        product.updateImageUrl("/images/${TSID.fast()}$extension")
        return ProductImageResponse.from(product)
    }
}
