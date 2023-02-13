package org.acme.scrm.controller

import org.acme.scrm.service.StudentService
import org.acme.scrm.service.dto.StudentDto
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(StudentController::class)
@ContextConfiguration(classes = [WebMvcTestMockkConfiguration::class])
class StudentControllerTestMockk(@Autowired val mockMvc: MockMvc) {

    private val studentDto = StudentDto(1, "f", "l", "test@test.de", mutableSetOf())

    @Autowired
    private lateinit var studentService: StudentService

    @Test
    fun testFindByEmail() {
        every { studentService.findByEmail("test@test.de") } returns studentDto

        mockMvc.perform(
            get("/api/v1/student/findByEmail?email=test@test.de")
                .contentType("application/json;charset=UTF-8")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
    }
}

@TestConfiguration
class WebMvcTestMockkConfiguration {

    @Bean
    @Primary
    fun mockStudentService(): StudentService = mockk()
}
