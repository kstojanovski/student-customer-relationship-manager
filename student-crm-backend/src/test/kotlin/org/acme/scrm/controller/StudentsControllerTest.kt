package org.acme.scrm.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.acme.scrm.service.StudentService
import org.acme.scrm.service.dto.StudentDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

private const val EMAIL = "test@test.de"
private const val FIRSTNAME = "firstname"
private const val LASTNAME = "lastname"

private const val APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8"

private const val APPLICATION_JSON = "application/json"

private const val COURSE_NAME = "cname"

@WebMvcTest(StudentsController::class)
class StudentsControllerTest(@Autowired val mockMvc: MockMvc) {

    private lateinit var mockStudentDto: StudentDto
    private lateinit var mockStudentDtoCollection: Set<StudentDto>

    @MockBean
    private lateinit var studentService: StudentService

    @BeforeEach
    fun init() {
        mockStudentDto = StudentDto(1, FIRSTNAME, LASTNAME, EMAIL, mutableSetOf())
        mockStudentDtoCollection = setOf(mockStudentDto)
    }

    @Test
    fun testGetAll() {
        `when`(studentService.findAllStudents()).thenReturn(mockStudentDtoCollection)

        mockMvc.perform(
            get("/api/v1/students/getAll").contentType(APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk).andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(json(mockStudentDtoCollection)))
    }

    @Test
    fun testFindByCourseName() {
        `when`(studentService.findByCourseName(COURSE_NAME)).thenReturn(mockStudentDtoCollection)

        mockMvc.perform(
            get("/api/v1/students/findByCourseName?courseName=$COURSE_NAME").contentType(APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk).andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(json(mockStudentDtoCollection)))
    }

    private fun json(any: Any): String = objectMapper.writeValueAsString(any)

    private val objectMapper = ObjectMapper()
}
