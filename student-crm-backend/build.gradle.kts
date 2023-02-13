import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    java
    id("org.springframework.boot") version "2.7.3"
    id("io.spring.dependency-management") version "1.0.13.RELEASE"
    kotlin("plugin.spring") version "1.7.10"
    kotlin("plugin.jpa") version "1.7.10"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "org.acme"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("io.mockk:mockk:1.13.2")
    testImplementation("org.testcontainers:junit-jupiter:1.17.4")
    testImplementation("org.testcontainers:postgresql:1.17.4")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.asciidoctor {
    dependsOn(tasks.test.get())
    sourceDir("src/main/asciidoc")
    attributes(mapOf("snippets" to "build/generated-snippets"))
    setOutputDir("doc")
}

tasks.jar {
    dependsOn(tasks.asciidoctor.get())
    val asciidoctor by tasks.getting(org.asciidoctor.gradle.jvm.AsciidoctorTask::class)
    from("${asciidoctor.outputDir}") {
        into("/doc")
    }
}

tasks.bootJar {
    dependsOn(tasks.asciidoctor.get())
    val asciidoctor by tasks.getting(org.asciidoctor.gradle.jvm.AsciidoctorTask::class)
    from("${asciidoctor.outputDir}") {
        into("/doc")
    }
}

val dockerUsername = project.properties["username"]
val dockerPassword = project.properties["password"]

tasks.bootBuildImage {
    imageName = "$dockerUsername/${project.name}:${project.version}"
    isPublish = true
    docker {
        publishRegistry {
            username = "$dockerUsername"
            password = "$dockerPassword"
        }
    }
}
