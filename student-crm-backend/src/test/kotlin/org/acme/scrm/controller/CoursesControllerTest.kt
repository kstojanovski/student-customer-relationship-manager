package org.acme.scrm.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.acme.scrm.service.CourseService
import org.acme.scrm.service.dto.CourseDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

private const val COURSE_NAME = "cname"

private const val APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8"

private const val APPLICATION_JSON = "application/json"

private const val EMAIL = "test.test@test.de"

@WebMvcTest(CoursesController::class)
class CoursesControllerTest(@Autowired val mockMvc: MockMvc) {

    private lateinit var mockCourseDto: CourseDto
    private lateinit var mockCourseDtoCollection: Set<CourseDto>

    @MockBean
    private lateinit var courseService: CourseService

    @BeforeEach
    fun init() {
        mockCourseDto = CourseDto(1, COURSE_NAME, mutableSetOf())
        mockCourseDtoCollection = setOf(mockCourseDto)
    }

    @Test
    fun testGetAll() {
        Mockito.`when`(courseService.findAllCourses()).thenReturn(mockCourseDtoCollection)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/courses/getAll").contentType(APPLICATION_JSON_UTF8)
        ).andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().json(json(mockCourseDtoCollection)))
    }

    @Test
    fun testFindByCourseName() {
        Mockito.`when`(courseService.findByStudentEmail(EMAIL)).thenReturn(mockCourseDtoCollection)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/courses/findByStudentEmail?email=$EMAIL")
                .contentType(APPLICATION_JSON_UTF8)
        ).andExpect(MockMvcResultMatchers.status().isOk).andExpect(
            MockMvcResultMatchers.content().contentType(APPLICATION_JSON)
        ).andExpect(MockMvcResultMatchers.content().json(json(mockCourseDtoCollection)))
    }

    private fun json(any: Any): String = objectMapper.writeValueAsString(any)

    private val objectMapper = ObjectMapper()
}
