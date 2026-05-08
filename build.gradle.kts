plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "3.5.14"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.2.21"
    id("com.epages.restdocs-api-spec") version "0.19.4"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val snippetsDir = file("build/generated-snippets")

openapi3 {
    setServer("http://localhost:8080")
    title = "REST Docs Kotlin DSL API"
    version = "0.0.1"
    format = "yaml"
    outputFileNamePrefix = "openapi3"
    outputDirectory = "build/api-spec"
    snippetsDirectory = "build/generated-snippets"
}

dependencies {
    // Web & Validation
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // JPA & H2
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")

    // Tsid
    implementation("io.hypersistence:hypersistence-utils-hibernate-63:3.15.2")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Development
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.17")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.4")

    // Logging
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
}

configurations.all {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    exclude(group = "org.apache.logging.log4j", module = "log4j-to-slf4j")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
    outputs.dir(snippetsDir)
}

tasks.test {
    systemProperty("org.springframework.restdocs.outputDir", snippetsDir.absolutePath)
}

afterEvaluate {
    tasks.named("openapi3") { dependsOn(tasks.test); group = null }
    tasks.named("openapi") { group = null; enabled = false }
    tasks.named("postman") { group = null; enabled = false }
}

val copyOpenApiSpec by tasks.registering(Copy::class) {
    dependsOn("openapi3")
    from("build/api-spec/openapi3.yaml")
    into(layout.buildDirectory.dir("resources/main/static/docs"))
}

val generateDocs by tasks.registering {
    group = "documentation"
    description = "테스트 실행 → OpenAPI 3.0 YAML 생성 → static/docs 복사"
    dependsOn("copyOpenApiSpec")
}

tasks.bootJar { dependsOn("copyOpenApiSpec") }
tasks.bootRun { dependsOn("copyOpenApiSpec") }
tasks.build { dependsOn("copyOpenApiSpec") }

tasks.named<Jar>("jar") {
    enabled = false
}
