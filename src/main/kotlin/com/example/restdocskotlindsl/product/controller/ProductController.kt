package com.example.restdocskotlindsl.product.controller

import com.example.restdocskotlindsl.common.response.ApiResponse
import com.example.restdocskotlindsl.product.domain.enums.Category
import com.example.restdocskotlindsl.product.dto.request.CreateProductRequest
import com.example.restdocskotlindsl.product.dto.request.UpdateProductRequest
import com.example.restdocskotlindsl.product.dto.response.ProductImageResponse
import com.example.restdocskotlindsl.product.dto.response.ProductResponse
import com.example.restdocskotlindsl.product.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @Valid @RequestBody request: CreateProductRequest,
    ): ApiResponse<ProductResponse> {
        val result = productService.create(request)

        return ApiResponse.success(result)
    }

    @GetMapping
    fun findAll(
        @RequestParam(required = false) category: Category?,
        @RequestParam(required = false) minPrice: Long?,
        @RequestParam(required = false) maxPrice: Long?,
    ): ApiResponse<List<ProductResponse>> {
        val result = productService.findAll(category, minPrice, maxPrice)

        return ApiResponse.success(result)
    }

    @GetMapping("/{productId}")
    fun findById(
        @PathVariable productId: Long,
    ): ApiResponse<ProductResponse> {
        val result = productService.findById(productId)

        return ApiResponse.success(result)
    }

    @PatchMapping("/{productId}")
    fun update(
        @PathVariable productId: Long,
        @Valid @RequestBody request: UpdateProductRequest,
    ): ApiResponse<ProductResponse> {
        val result = productService.update(productId, request)

        return ApiResponse.success(result)
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @PathVariable productId: Long,
    ) = productService.delete(productId)

    @PostMapping("/{productId}/images", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(
        @PathVariable productId: Long,
        @RequestParam("image") image: MultipartFile,
    ): ApiResponse<ProductImageResponse> {
        val result = productService.uploadImage(productId, image)

        return ApiResponse.success(result)
    }
}
