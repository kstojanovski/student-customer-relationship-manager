package org.acme.scrm.integrationtests

import org.flywaydb.core.Flyway
import org.junit.jupiter.api.BeforeAll
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles("test-containers-flyway")
@Testcontainers
interface FlywayPostgresTestContainer {

    @Component
    companion object {
        private val postgresContainer = PostgreSQLContainer<Nothing>("postgres:latest")

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            postgresContainer.start()
            val flyway = Flyway.configure().dataSource(
                postgresContainer.jdbcUrl,
                postgresContainer.username,
                postgresContainer.password
            ).load()
            flyway.migrate()
        }

        @Suppress("unused")
        @DynamicPropertySource
        @JvmStatic
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
        }
    }
}
