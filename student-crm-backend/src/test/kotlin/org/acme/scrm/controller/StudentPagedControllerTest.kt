package org.acme.scrm.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.acme.scrm.service.StudentsPagedService
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
private const val COURSE_NAME = "cname"

private const val APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8"

private const val APPLICATION_JSON = "application/json"

private const val PAGE = 0

private const val SIZE = 5

@WebMvcTest(StudentsPagedController::class)
class StudentPagedControllerTest(@Autowired val mockMvc: MockMvc) {

    private lateinit var studentDto: StudentDto
    private lateinit var studentDtoCollection: Set<StudentDto>

    @MockBean
    private lateinit var studentsPagedService: StudentsPagedService

    @BeforeEach
    fun init() {
        studentDto = StudentDto(1, FIRSTNAME, LASTNAME, EMAIL, mutableSetOf())
        studentDtoCollection = setOf(studentDto)
    }

    @Test
    fun testFindByFirstnameContaining() {
        `when`(studentsPagedService.findByFirstnameContaining(FIRSTNAME, PAGE, SIZE)).thenReturn(studentDtoCollection)
        mockMvc.perform(
            get("/api/v1/students/paged/findByFirstname?firstname=$FIRSTNAME&page=$PAGE&size=$SIZE")
                .contentType(APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk).andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(json(studentDtoCollection)))
    }

    @Test
    fun testFindByLastnameContaining() {
        `when`(studentsPagedService.findByLastnameContaining(LASTNAME, PAGE, SIZE)).thenReturn(studentDtoCollection)
        mockMvc.perform(
            get("/api/v1/students/paged/findByLastname?lastname=$LASTNAME&page=$PAGE&size=$SIZE")
                .contentType(APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk).andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(json(studentDtoCollection)))
    }

    @Test
    fun testFindByNameContaining() {
        `when`(studentsPagedService.findByFirstnameContainingOrLastnameContaining(FIRSTNAME, LASTNAME, PAGE, SIZE))
            .thenReturn(studentDtoCollection)
        mockMvc.perform(
            get("/api/v1/students/paged/findByName?firstname=$FIRSTNAME&lastname=$LASTNAME&page=$PAGE&size=$SIZE")
                .contentType(APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk).andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(json(studentDtoCollection)))
    }

    @Test
    fun testFindByCourseName() {
        `when`(studentsPagedService.findByCourseName(COURSE_NAME, PAGE, SIZE))
            .thenReturn(studentDtoCollection)
        mockMvc.perform(
            get("/api/v1/students/paged/findByCourseName?courseName=$COURSE_NAME&page=$PAGE&size=$SIZE")
                .contentType(APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk).andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(json(studentDtoCollection)))
    }

    @Test
    fun testFindByCourseNameNative() {
        `when`(studentsPagedService.findByCourseNameNative(COURSE_NAME, PAGE, SIZE))
            .thenReturn(studentDtoCollection)
        mockMvc.perform(
            get("/api/v1/students/paged/findByCourseNameNative?courseName=$COURSE_NAME&page=$PAGE&size=$SIZE")
                .contentType(APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk).andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(json(studentDtoCollection)))
    }

    private fun json(any: Any): String = objectMapper.writeValueAsString(any)

    private val objectMapper = ObjectMapper()
}
