package org.acme.scrm.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.acme.scrm.exception.CourseAlreadyExistsException
import org.acme.scrm.exception.CourseNotFoundException
import org.acme.scrm.exception.EntityAlreadyRelatedException
import org.acme.scrm.exception.StudentNotFoundException
import org.acme.scrm.exception.handler.ControllerExceptionHandler
import org.acme.scrm.service.CourseService
import org.acme.scrm.service.dto.CourseDto
import org.acme.scrm.service.dto.NewCourseDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.stream.Stream

private const val COURSE_NAME = "cname"

private const val APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8"

private const val APPLICATION_JSON = "application/json"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(CourseController::class)
class CourseControllerTest(@Autowired val mockMvc: MockMvc) {

    private lateinit var mockNewCourseDto: NewCourseDto
    private lateinit var mockCourseDto: CourseDto
    private lateinit var mockCourseDtoCollection: Set<CourseDto>

    @MockBean
    private lateinit var courseService: CourseService

    @BeforeEach
    fun init() {
        mockNewCourseDto = NewCourseDto(COURSE_NAME)
        mockCourseDto = CourseDto(1, mockNewCourseDto.name, mutableSetOf())
        mockCourseDtoCollection = setOf(mockCourseDto)
    }

    @Test
    fun testCreateCourse() {
        `when`(courseService.saveCourse(mockNewCourseDto)).thenReturn(mockCourseDto)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/course/create")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(json(mockNewCourseDto))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType(APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.content().json(json(mockCourseDto)))
    }

    @Test
    fun testCreateCourseException() {
        val courseAlreadyExistsException =
            CourseAlreadyExistsException("On create course it was found with the name $COURSE_NAME")
        `when`(courseService.saveCourse(mockNewCourseDto))
            .thenThrow(courseAlreadyExistsException)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/v1/course/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(json(mockNewCourseDto))
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string(errorMessage(courseAlreadyExistsException)))
    }

    @Test
    fun testFindStudent() {
        `when`(courseService.findCourse(mockCourseDto)).thenReturn(mockCourseDto)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/course/find")
                .contentType(APPLICATION_JSON_UTF8)
                .content(json(mockCourseDto))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.content().json(json(mockCourseDto)))
    }

    @Test
    fun testFindStudentStudentNotFoundException() {
        val courseNotFoundException = CourseNotFoundException("Course not found!")
        `when`(courseService.findCourse(mockCourseDto))
            .thenThrow(courseNotFoundException)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/course/find")
                .contentType(APPLICATION_JSON_UTF8)
                .content(json(mockCourseDto))
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string(errorMessage(courseNotFoundException)))
    }

    @Test
    fun testFindByEmail() {
        `when`(courseService.findByName(COURSE_NAME)).thenReturn(mockCourseDto)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/course/findByName?name=$COURSE_NAME")
                .contentType(APPLICATION_JSON_UTF8)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.content().json(json(mockCourseDto)))
    }

    @Test
    fun testFindByEmailException() {
        val courseNotFoundException = CourseNotFoundException("Course not found!")
        `when`(courseService.findByName(COURSE_NAME)).thenThrow(courseNotFoundException)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/v1/course/findByName?name=$COURSE_NAME")
                .contentType(APPLICATION_JSON_UTF8)
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string(errorMessage(courseNotFoundException)))
    }

    @Test
    fun testUpdate() {
        `when`(courseService.update(mockCourseDto)).thenReturn(mockCourseDto)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/course/update")
                .contentType(APPLICATION_JSON_UTF8)
                .content(json(mockCourseDto))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.content().json(json(mockCourseDto)))
    }

    @Test
    fun testRelateToCourses() {
        `when`(courseService.checkAndRelateToStudent(mockCourseDto, setOf(1, 2, 3))).thenReturn(mockCourseDto)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/course/relateToStudent?studentIds=1,2,3")
                .contentType(APPLICATION_JSON_UTF8)
                .content(json(mockCourseDto))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(MockMvcResultMatchers.content().json(json(mockCourseDto)))
    }

    @ParameterizedTest
    @MethodSource("exceptionCases")
    fun testRelateToCoursesExceptions(useCase: String) {
        val courseNotFoundException: CourseNotFoundException
        val entityAlreadyRelatedException: EntityAlreadyRelatedException
        val studentNotFoundException: StudentNotFoundException
        val errorMessage: String

        val invokeCheckAndRelate = `when`(courseService.checkAndRelateToStudent(mockCourseDto, setOf(1, 2, 3)))
        when (useCase) {
            "CourseNotFoundException - Course does not exists" -> {
                courseNotFoundException = CourseNotFoundException("On updating the student the course is not found!")
                invokeCheckAndRelate.thenThrow(courseNotFoundException)
                errorMessage = errorMessage(courseNotFoundException)
            }

            "EntityAlreadyRelatedException - The Course is already related to the student" -> {
                entityAlreadyRelatedException =
                    EntityAlreadyRelatedException("StudentDto is already related to the course!")
                invokeCheckAndRelate.thenThrow(entityAlreadyRelatedException)
                errorMessage = errorMessage(entityAlreadyRelatedException)
            }

            "StudentNotFoundException - The Student does not exits" -> {
                studentNotFoundException = StudentNotFoundException("StudentDto not found for updating!")
                invokeCheckAndRelate.thenThrow(studentNotFoundException)
                errorMessage = errorMessage(studentNotFoundException)
            }

            else -> {
                fail("invalid case")
            }
        }

        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/v1/course/relateToStudent?studentIds=1,2,3")
                .contentType(APPLICATION_JSON_UTF8)
                .content(json(mockCourseDto))
        )
            .andExpect(MockMvcResultMatchers.status().is4xxClientError)
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.content().string(errorMessage))
    }

    @Suppress("unused")
    private fun exceptionCases(): Stream<Arguments> =
        Stream.of(
            Arguments.of("CourseNotFoundException - Course does not exists"),
            Arguments.of("EntityAlreadyRelatedException - The Course is already related to the student"),
            Arguments.of("StudentNotFoundException - The Student does not exits")
        )

    @Test
    fun testDelete() {
        doNothing().`when`(courseService).delete(mockCourseDto)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/course/delete")
                .contentType(APPLICATION_JSON_UTF8)
                .content(json(mockCourseDto))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun testDeleteByEmail() {
        doNothing().`when`(courseService).deleteByName(mockCourseDto.name)

        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/v1/course/deleteByName")
                .contentType(APPLICATION_JSON_UTF8)
                .content(json(mockCourseDto))
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    private fun errorMessage(exception: Exception) = ControllerExceptionHandler().errorMessage(exception)

    private fun json(any: Any): String =
        objectMapper.writeValueAsString(any)

    private val objectMapper = ObjectMapper()
}
