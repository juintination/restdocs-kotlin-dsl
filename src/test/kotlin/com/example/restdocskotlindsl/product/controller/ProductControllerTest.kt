package com.example.restdocskotlindsl.product.controller

import com.example.restdocskotlindsl.product.domain.Product
import com.example.restdocskotlindsl.product.domain.enums.Category
import com.example.restdocskotlindsl.product.repository.ProductRepository
import com.example.restdocskotlindsl.support.RestDocsMvcTest
import com.example.restdocskotlindsl.support.dsl.restDocs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType

class ProductControllerTest : RestDocsMvcTest() {

    @Autowired
    private lateinit var productRepository: ProductRepository

    private lateinit var product: Product

    @BeforeEach
    fun setup() {
        product = productRepository.save(
            Product.create(
                name = "상품명",
                description = "상품 설명",
                price = 10_000L,
                category = Category.ELECTRONICS,
                stock = 10,
            )
        )
    }

    @Test
    fun `상품 등록 API`() {
        restDocs(mockMvc, "products/create") {
            summary("상품 등록")
            request {
                post("/api/products")
                body {
                    field("name", "상품명", "상품명")
                    field("description", "상품 설명", "상품 설명")
                    field("price", 10_000L, "가격 (원)")
                    field("category", Category.ELECTRONICS, "카테고리 (ELECTRONICS, CLOTHING, FOOD, BOOKS)")
                    field("stock", 10, "재고 수량")
                }
            }
            response {
                status(201)
                body {
                    field("success", "요청 성공 여부", JsonFieldType.BOOLEAN)
                    obj("data", "응답 데이터") {
                        field("id", "생성된 상품 ID", JsonFieldType.NUMBER)
                        field("name", "상품명", JsonFieldType.STRING)
                        field("description", "상품 설명", JsonFieldType.STRING)
                        field("price", "가격 (원)", JsonFieldType.NUMBER)
                        field("category", "카테고리", JsonFieldType.STRING)
                        field("stock", "재고 수량", JsonFieldType.NUMBER)
                        field("imageUrl", "이미지 URL", JsonFieldType.STRING).optional()
                    }
                }
            }
        }
    }

    @Test
    fun `상품 목록 조회 API`() {
        restDocs(mockMvc, "products/list") {
            summary("상품 목록 조회")
            request {
                get("/api/products")
                queryParameters {
                    param("category", Category.ELECTRONICS.name, "카테고리 필터").optional()
                    param("minPrice", "1000", "최소 가격 필터").optional()
                    param("maxPrice", "50000", "최대 가격 필터").optional()
                }
            }
            response {
                status(200)
                body {
                    field("success", "요청 성공 여부", JsonFieldType.BOOLEAN)
                    array("data", "응답 데이터 목록") {
                        field("id", "상품 ID", JsonFieldType.NUMBER)
                        field("name", "상품명", JsonFieldType.STRING)
                        field("description", "상품 설명", JsonFieldType.STRING)
                        field("price", "가격 (원)", JsonFieldType.NUMBER)
                        field("category", "카테고리", JsonFieldType.STRING)
                        field("stock", "재고 수량", JsonFieldType.NUMBER)
                        field("imageUrl", "이미지 URL", JsonFieldType.STRING).optional()
                    }
                }
            }
        }
    }

    @Test
    fun `상품 단건 조회 API`() {
        restDocs(mockMvc, "products/get") {
            summary("상품 단건 조회")
            request {
                get("/api/products/{id}")
                pathParameters {
                    param("id", product.id.toString(), "상품 ID")
                }
            }
            response {
                status(200)
                body {
                    field("success", "요청 성공 여부", JsonFieldType.BOOLEAN)
                    obj("data", "응답 데이터") {
                        field("id", "상품 ID", JsonFieldType.NUMBER)
                        field("name", "상품명", JsonFieldType.STRING)
                        field("description", "상품 설명", JsonFieldType.STRING)
                        field("price", "가격 (원)", JsonFieldType.NUMBER)
                        field("category", "카테고리", JsonFieldType.STRING)
                        field("stock", "재고 수량", JsonFieldType.NUMBER)
                        field("imageUrl", "이미지 URL", JsonFieldType.STRING).optional()
                    }
                }
            }
        }
    }

    @Test
    fun `상품 정보 수정 API`() {
        restDocs(mockMvc, "products/update") {
            summary("상품 정보 수정")
            request {
                patch("/api/products/{id}")
                pathParameters {
                    param("id", product.id.toString(), "상품 ID")
                }
                body {
                    field("name", "수정된 상품명", "변경할 상품명").optional()
                    field("price", 20_000L, "변경할 가격 (원)").optional()
                    field("stock", 5, "변경할 재고 수량").optional()
                }
            }
            response {
                status(200)
                body {
                    field("success", "요청 성공 여부", JsonFieldType.BOOLEAN)
                    obj("data", "응답 데이터") {
                        field("id", "상품 ID", JsonFieldType.NUMBER)
                        field("name", "상품명", JsonFieldType.STRING)
                        field("description", "상품 설명", JsonFieldType.STRING)
                        field("price", "가격 (원)", JsonFieldType.NUMBER)
                        field("category", "카테고리", JsonFieldType.STRING)
                        field("stock", "재고 수량", JsonFieldType.NUMBER)
                        field("imageUrl", "이미지 URL", JsonFieldType.STRING).optional()
                    }
                }
            }
        }
    }

    @Test
    fun `상품 삭제 API`() {
        restDocs(mockMvc, "products/delete") {
            summary("상품 삭제")
            request {
                delete("/api/products/{id}")
                pathParameters {
                    param("id", product.id.toString(), "상품 ID")
                }
            }
            response {
                status(204)
            }
        }
    }

    @Test
    fun `상품 이미지 업로드 API`() {
        restDocs(mockMvc, "products/upload-image") {
            summary("상품 이미지 업로드")
            request {
                post("/api/products/{id}/images")
                pathParameters {
                    param("id", product.id.toString(), "상품 ID")
                }
                multipart {
                    file(
                        paramName = "image",
                        originalFilename = "product.jpg",
                        contentType = MediaType.IMAGE_JPEG_VALUE,
                        content = "fake-image-bytes".toByteArray(),
                    )
                }
            }
            response {
                status(200)
                body {
                    field("success", "요청 성공 여부", JsonFieldType.BOOLEAN)
                    obj("data", "응답 데이터") {
                        field("id", "상품 ID", JsonFieldType.NUMBER)
                        field("imageUrl", "저장된 이미지 경로", JsonFieldType.STRING)
                    }
                }
            }
        }
    }
}
