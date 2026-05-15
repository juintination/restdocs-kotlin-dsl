package com.example.restdocskotlindsl.product.controller

import com.epages.restdocs.apispec.ResourceDocumentation
import com.epages.restdocs.apispec.ResourceSnippetParameters
import com.example.restdocskotlindsl.product.domain.Product
import com.example.restdocskotlindsl.product.domain.enums.Category
import com.example.restdocskotlindsl.product.repository.ProductRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@ExtendWith(RestDocumentationExtension::class, SpringExtension::class)
@Transactional
class ProductControllerTestWithoutDsl {

    @Autowired
    private lateinit var context: WebApplicationContext

    @Autowired
    private lateinit var productRepository: ProductRepository

    private lateinit var mockMvc: MockMvc
    
    private lateinit var product: Product

    @BeforeEach
    fun setUp(
        provider: RestDocumentationContextProvider,
    ) {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(
                MockMvcRestDocumentation.documentationConfiguration(provider)
                    .operationPreprocessors()
                    .withRequestDefaults(Preprocessors.prettyPrint())
                    .withResponseDefaults(Preprocessors.prettyPrint())
            )
            .build()

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
        mockMvc.perform(
            RestDocumentationRequestBuilders.post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "상품명",
                        "description": "상품 설명",
                        "price": 10000,
                        "category": "ELECTRONICS",
                        "stock": 10
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isCreated)
            .andDo(
                document(
                    "products-without-dsl/create",
                    requestFields(
                        fieldWithPath("name").description("상품명"),
                        fieldWithPath("description").description("상품 설명"),
                        fieldWithPath("price").description("가격 (원)"),
                        fieldWithPath("category").description("카테고리 (ELECTRONICS, CLOTHING, FOOD, BOOKS)"),
                        fieldWithPath("stock").description("재고 수량"),
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 상품 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("상품명"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격 (원)"),
                        fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                        fieldWithPath("data.stock").type(JsonFieldType.NUMBER).description("재고 수량"),
                        fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).description("이미지 URL").optional(),
                    ),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .tag("Products")
                            .summary("상품 등록")
                            .requestFields(
                                fieldWithPath("name").description("상품명"),
                                fieldWithPath("description").description("상품 설명"),
                                fieldWithPath("price").description("가격 (원)"),
                                fieldWithPath("category").description("카테고리 (ELECTRONICS, CLOTHING, FOOD, BOOKS)"),
                                fieldWithPath("stock").description("재고 수량"),
                            )
                            .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("생성된 상품 ID"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("상품명"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격 (원)"),
                                fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("data.stock").type(JsonFieldType.NUMBER).description("재고 수량"),
                                fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).description("이미지 URL")
                                    .optional(),
                            )
                            .build()
                    ),
                )
            )
    }

    @Test
    fun `상품 목록 조회 API`() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/products")
                .param("category", "ELECTRONICS")
                .param("minPrice", "1000")
                .param("maxPrice", "50000")
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "products-without-dsl/list",
                    queryParameters(
                        parameterWithName("category").description("카테고리 필터").optional(),
                        parameterWithName("minPrice").description("최소 가격 필터").optional(),
                        parameterWithName("maxPrice").description("최대 가격 필터").optional(),
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답 데이터 목록"),
                        fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("상품 ID"),
                        fieldWithPath("data[].name").type(JsonFieldType.STRING).description("상품명"),
                        fieldWithPath("data[].description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("가격 (원)"),
                        fieldWithPath("data[].category").type(JsonFieldType.STRING).description("카테고리"),
                        fieldWithPath("data[].stock").type(JsonFieldType.NUMBER).description("재고 수량"),
                        fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING).description("이미지 URL").optional(),
                    ),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .tag("Products")
                            .summary("상품 목록 조회")
                            .queryParameters(
                                ResourceDocumentation.parameterWithName("category").description("카테고리 필터").optional(),
                                ResourceDocumentation.parameterWithName("minPrice").description("최소 가격 필터").optional(),
                                ResourceDocumentation.parameterWithName("maxPrice").description("최대 가격 필터").optional(),
                            )
                            .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답 데이터 목록"),
                                fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data[].name").type(JsonFieldType.STRING).description("상품명"),
                                fieldWithPath("data[].description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("가격 (원)"),
                                fieldWithPath("data[].category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("data[].stock").type(JsonFieldType.NUMBER).description("재고 수량"),
                                fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING).description("이미지 URL")
                                    .optional(),
                            )
                            .build()
                    ),
                )
            )
    }

    @Test
    fun `상품 단건 조회 API`() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.get("/api/products/{id}", product.id)
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "products-without-dsl/get",
                    pathParameters(
                        parameterWithName("id").description("상품 ID"),
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("상품명"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격 (원)"),
                        fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                        fieldWithPath("data.stock").type(JsonFieldType.NUMBER).description("재고 수량"),
                        fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).description("이미지 URL").optional(),
                    ),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .tag("Products")
                            .summary("상품 단건 조회")
                            .pathParameters(
                                ResourceDocumentation.parameterWithName("id").description("상품 ID"),
                            )
                            .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("상품명"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격 (원)"),
                                fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("data.stock").type(JsonFieldType.NUMBER).description("재고 수량"),
                                fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).description("이미지 URL")
                                    .optional(),
                            )
                            .build()
                    ),
                )
            )
    }

    @Test
    fun `상품 정보 수정 API`() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.patch("/api/products/{id}", product.id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                        "name": "수정된 상품명",
                        "price": 20000,
                        "stock": 5
                    }
                    """.trimIndent()
                )
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "products-without-dsl/update",
                    pathParameters(
                        parameterWithName("id").description("상품 ID"),
                    ),
                    requestFields(
                        fieldWithPath("name").description("변경할 상품명").optional(),
                        fieldWithPath("price").description("변경할 가격 (원)").optional(),
                        fieldWithPath("stock").description("변경할 재고 수량").optional(),
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 ID"),
                        fieldWithPath("data.name").type(JsonFieldType.STRING).description("상품명"),
                        fieldWithPath("data.description").type(JsonFieldType.STRING).description("상품 설명"),
                        fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격 (원)"),
                        fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                        fieldWithPath("data.stock").type(JsonFieldType.NUMBER).description("재고 수량"),
                        fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).description("이미지 URL").optional(),
                    ),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .tag("Products")
                            .summary("상품 정보 수정")
                            .pathParameters(
                                ResourceDocumentation.parameterWithName("id").description("상품 ID"),
                            )
                            .requestFields(
                                fieldWithPath("name").description("변경할 상품명").optional(),
                                fieldWithPath("price").description("변경할 가격 (원)").optional(),
                                fieldWithPath("stock").description("변경할 재고 수량").optional(),
                            )
                            .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data.name").type(JsonFieldType.STRING).description("상품명"),
                                fieldWithPath("data.description").type(JsonFieldType.STRING).description("상품 설명"),
                                fieldWithPath("data.price").type(JsonFieldType.NUMBER).description("가격 (원)"),
                                fieldWithPath("data.category").type(JsonFieldType.STRING).description("카테고리"),
                                fieldWithPath("data.stock").type(JsonFieldType.NUMBER).description("재고 수량"),
                                fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).description("이미지 URL")
                                    .optional(),
                            )
                            .build()
                    ),
                )
            )
    }

    @Test
    fun `상품 삭제 API`() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/products/{id}", product.id)
        )
            .andExpect(status().isNoContent)
            .andDo(
                document(
                    "products-without-dsl/delete",
                    pathParameters(
                        parameterWithName("id").description("상품 ID"),
                    ),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .tag("Products")
                            .summary("상품 삭제")
                            .pathParameters(
                                ResourceDocumentation.parameterWithName("id").description("상품 ID"),
                            )
                            .build()
                    ),
                )
            )
    }

    @Test
    fun `상품 이미지 업로드 API`() {
        mockMvc.perform(
            RestDocumentationRequestBuilders.multipart("/api/products/{id}/images", product.id)
                .file(
                    MockMultipartFile(
                        "image",
                        "product.jpg",
                        MediaType.IMAGE_JPEG_VALUE,
                        "fake-image-bytes".toByteArray()
                    )
                )
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "products-without-dsl/upload-image",
                    pathParameters(
                        parameterWithName("id").description("상품 ID"),
                    ),
                    responseFields(
                        fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                        fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 ID"),
                        fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).description("저장된 이미지 경로"),
                    ),
                    ResourceDocumentation.resource(
                        ResourceSnippetParameters.builder()
                            .tag("Products")
                            .summary("상품 이미지 업로드")
                            .pathParameters(
                                ResourceDocumentation.parameterWithName("id").description("상품 ID"),
                            )
                            .responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("요청 성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 ID"),
                                fieldWithPath("data.imageUrl").type(JsonFieldType.STRING).description("저장된 이미지 경로"),
                            )
                            .build()
                    ),
                )
            )
    }
}
