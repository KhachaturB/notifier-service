
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Target
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

plugins {
    id("org.springframework.boot") version "3.5.10"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.spring") version "1.9.23"
    id("org.jooq.jooq-codegen-gradle") version "3.20.11"
    id("com.diffplug.spotless") version "8.2.1"
    id("com.google.cloud.tools.jib") version "3.4.4"
    id("jacoco")
}

group = "ru.vachoo"
val appVersion: String? = System.getenv("APP_VERSION")
version = appVersion ?: "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencyManagement {
    applyMavenExclusions(false)
    imports {
        mavenBom(BOM_COORDINATES)
        mavenBom("org.springframework.ai:spring-ai-bom:1.1.4")
    }
}

val coroutines: String by project
val swagger: String by project
val modelMapper: String by project
val liquibase: String by project
val postgres: String by project
val jooq: String by project

buildscript {
    val postgres: String by project

    dependencies {
        classpath("org.postgresql:postgresql:$postgres")
    }
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.ai:spring-ai-starter-model-openai")

    // Database
    implementation("org.liquibase:liquibase-core:$liquibase")
    implementation("org.postgresql:postgresql:$postgres")
    implementation("org.jooq:jooq:$jooq")
    // implementation("org.jooq:jooq-jackson-extensions:$jooq")

    // Utils
    implementation("org.modelmapper:modelmapper:$modelMapper")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$swagger")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.3.1")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    classDirectories.setFrom(
        sourceSets.main.get().output.asFileTree.matching {
            exclude("ru/vachoo/notifier/adapter/out/db/generated/**")
        }
    )
}

spotless {
    kotlin {
        ktfmt("0.51").googleStyle()
        targetExclude("**/generated/**/*.*")
    }
}

tasks.register("jooq-codegen") {
    doLast {
        val configuration = org.jooq.meta.jaxb.Configuration()
            .withLogging(org.jooq.meta.jaxb.Logging.INFO)
            .withJdbc(
                Jdbc()
                .withDriver("org.postgresql.Driver")
                .withUrl("jdbc:postgresql://localhost:5432/postgres")
                .withUser("secret")
                .withPassword("secret")
            )
            .withGenerator(
                Generator()
                    .withName("org.jooq.codegen.KotlinGenerator")
                    .withDatabase(
                        Database()
                            .withName("org.jooq.meta.postgres.PostgresDatabase")
                            .withInputSchema("notifier_service")
                            .withExcludes("databasechangelog|databasechangeloglock")
                            .withForcedTypes(
                                ForcedType()
                                    .withUserType("com.fasterxml.jackson.databind.JsonNode")
                                    .withJsonConverter(true)
                                    .withIncludeTypes("json")
                            )
                    )
                    .withTarget(
                        Target()
                            .withPackageName("ru.vachoo.notifier.adapter.out.db.generated")
                            .withDirectory("$projectDir/src/main/kotlin")
                    )
            )
        GenerationTool.generate(configuration)
    }
}

jib {
    from {
        image = "eclipse-temurin:21-jre-alpine"
    }
    to {
        image = "ghcr.io/khachaturb/notifier-service"
        tags = setOf(version.toString(), "latest")
    }
    container {
        mainClass = "org.springframework.boot.loader.launch.JarLauncher"
        ports = listOf("8080")
        environment = mapOf(
            "SPRING_DATASOURCE_URL" to "\${SPRING_DATASOURCE_URL}",
            "SPRING_DATASOURCE_USERNAME" to "\${SPRING_DATASOURCE_USERNAME}",
            "SPRING_DATASOURCE_PASSWORD" to "\${SPRING_DATASOURCE_PASSWORD}",
            "NIM_API_KEY" to "\${NIM_API_KEY}"
        )
    }
}
