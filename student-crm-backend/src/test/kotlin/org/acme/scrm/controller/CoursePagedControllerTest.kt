package org.acme.scrm.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.acme.scrm.service.CoursesPagedService
import org.acme.scrm.service.dto.CourseDto
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
private const val COURSE_NAME = "cname"

private const val APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8"

private const val APPLICATION_JSON = "application/json"

private const val PAGE = 0

private const val SIZE = 5

@WebMvcTest(CoursesPagedController::class)
class CoursePagedControllerTest(@Autowired val mockMvc: MockMvc) {

    private lateinit var courseDto: CourseDto
    private lateinit var courseDtoCollection: Set<CourseDto>

    @MockBean
    private lateinit var coursesPagedService: CoursesPagedService

    @BeforeEach
    fun init() {
        courseDto = CourseDto(1, COURSE_NAME, mutableSetOf())
        courseDtoCollection = setOf(courseDto)
    }

    @Test
    fun testFindByStudentEmail() {
        `when`(coursesPagedService.findByStudentEmail(EMAIL, PAGE, SIZE)).thenReturn(courseDtoCollection)
        mockMvc.perform(
            get("/api/v1/courses/paged/findByStudentEmail?email=$EMAIL&page=$PAGE&size=$SIZE")
                .contentType(APPLICATION_JSON_UTF8)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(json(courseDtoCollection)))
    }

    @Test
    fun testFindByStudentEmailNative() {
        `when`(coursesPagedService.findByStudentEmailNative(EMAIL, PAGE, SIZE)).thenReturn(courseDtoCollection)
        mockMvc.perform(
            get("/api/v1/courses/paged/findByStudentEmailNative?email=$EMAIL&page=$PAGE&size=$SIZE")
                .contentType(APPLICATION_JSON_UTF8)
        ).andExpect(status().isOk)
            .andExpect(content().contentType(APPLICATION_JSON))
            .andExpect(content().json(json(courseDtoCollection)))
    }

    private fun json(any: Any): String = objectMapper.writeValueAsString(any)

    private val objectMapper = ObjectMapper()
}
