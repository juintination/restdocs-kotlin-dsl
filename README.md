# REST Docs Kotlin DSL

REST Docs Kotlin DSL 기반 Swagger UI API 문서화 프로젝트

---

## DSL 구조

```
restDocs(mockMvc, "identifier") {
    summary("...")          // OpenAPI summary (생략 시 Swagger UI에 미반영)
    tag("...")              // OpenAPI tag (생략 시 identifier 앞부분에서 자동 추출)

    request {
        get / post / put / patch / delete("url")
        header("name", "value")
        pathParameters { param("name", "value", "description") }
        queryParameters { param("name", "value", "description") }
        body    { field("path", value, "description") }
        form    { field("name", "value", "description") }
        multipart { file(...) / text(...) }
    }

    response {
        status(200)
        body { field("path", "description", JsonFieldType.STRING) }
    }
}
```

### 사용 예시

#### JSON 요청/응답

```
restDocs(mockMvc, "foo/create") {
    summary("foo 등록")
    request {
        post("/api/foo")
        body {
            field("name", "이름", "이름")
            field("description", "설명", "설명")
        }
    }
    response {
        status(201)
        body {
            field("success", "요청 성공 여부", JsonFieldType.BOOLEAN)
            field("data.id", "생성된 ID", JsonFieldType.NUMBER)
            field("data.name", "이름", JsonFieldType.STRING)
            field("data.imageUrl", "이미지 URL").optional()
        }
    }
}
```

#### 경로 변수 + 쿼리 파라미터

```
restDocs(mockMvc, "foo/list") {
    summary("foo 목록 조회")
    request {
        get("/api/bar/{id}/foo")
        pathParameters {
            param("id", "1", "bar ID")
        }
        queryParameters {
            param("page", "1", "페이지 번호").optional()
            param("size", "10", "페이지 크기").optional()
        }
    }
    response {
        status(200)
        body {
            field("data", "목록", JsonFieldType.ARRAY)
            field("data[].id", "ID", JsonFieldType.NUMBER)
            field("data[].name", "이름", JsonFieldType.STRING)
        }
    }
}
```

#### 파일 업로드

```
restDocs(mockMvc, "foo/upload") {
    summary("파일 업로드")
    request {
        post("/api/foo/{id}/image")
        pathParameters {
            param("id", "1", "foo ID")
        }
        multipart {
            file(
                paramName = "image",
                originalFilename = "photo.jpg",
                contentType = MediaType.IMAGE_JPEG_VALUE,
                content = "...".toByteArray(),
            )
        }
    }
    response {
        status(200)
        body {
            field("data.imageUrl", "저장된 이미지 경로", JsonFieldType.STRING)
        }
    }
}
```

---

## DSL 상세

### `request { }`

| 메서드                                      | 설명                                                     |
|------------------------------------------|--------------------------------------------------------|
| `get / post / put / patch / delete(url)` | HTTP 메서드와 URL 템플릿 지정                                   |
| `header(name, value)`                    | 요청 헤더 추가                                               |
| `pathParameters { }`                     | URL 경로 변수 선언. 선언 순서가 URL 템플릿의 `{변수}` 순서와 일치해야 한다       |
| `queryParameters { }`                    | 쿼리 파라미터 선언. `value`를 `null`로 넘기면 요청에서 제외된다             |
| `body { }`                               | JSON 요청 바디. `field()` 선언 순서대로 JSON이 조립된다               |
| `form { }`                               | `application/x-www-form-urlencoded` 요청                 |
| `multipart { }`                          | `multipart/form-data` 요청. `file()`과 `text()`를 혼용할 수 있다 |

`body`, `form`, `multipart`를 함께 선언하면 `multipart → form → body` 순으로 하나만 적용된다.

### `response { }`

| 메서드            | 설명                                       |
|----------------|------------------------------------------|
| `status(code)` | 기대하는 HTTP 상태 코드. 생략 시 200                |
| `body { }`     | 응답 바디 필드 문서화. 실제 응답에 있는 필드를 빠짐없이 선언해야 한다 |

### `field().optional()`

요청·응답 모두 `field()` 뒤에 `.optional()`을 체이닝할 수 있다.

```
field("data.imageUrl", "이미지 URL").optional()
```

### `summary()` / `tag()`

`summary()`를 선언하면 `openapi-resource.json` 스니펫이 추가로 생성되어 Swagger UI의 operation summary에 반영된다.  
`tag()`를 생략하면 `identifier`의 첫 번째 세그먼트(`foo/create` → `Foo`)가 자동으로 사용된다.

---

## Swagger UI 문서 생성

```
./gradlew generateDocs
```

테스트 실행 → OpenAPI 3.0 YAML 생성 → `build/resources/main/static/docs/` 복사까지 한 번에 수행된다.  
이후 애플리케이션을 실행하면 `/swagger-ui/index.html`에서 문서를 확인할 수 있다.
