package org.acme.scrm.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.acme.scrm.exception.CourseNotFoundException
import org.acme.scrm.exception.EntityAlreadyRelatedException
import org.acme.scrm.exception.StudentAlreadyExistsException
import org.acme.scrm.exception.StudentNotFoundException
import org.acme.scrm.exception.handler.ControllerExceptionHandler
import org.acme.scrm.service.StudentService
import org.acme.scrm.service.dto.CourseDto
import org.acme.scrm.service.dto.NewStudentDto
import org.acme.scrm.service.dto.StudentDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.stream.Stream
import javax.validation.ConstraintViolationException

private const val EMAIL = "test@test.de"
private const val FIRSTNAME = "firstname"
private const val LASTNAME = "lastname"

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(StudentController::class)
class StudentControllerTest(@Autowired val mockMvc: MockMvc) {

    private lateinit var mockNewStudentDto: NewStudentDto
    private lateinit var mockStudentDto: StudentDto
    private lateinit var mockCourseDto: CourseDto

    private val controllerExceptionHandler = ControllerExceptionHandler()

    @MockBean
    private lateinit var studentService: StudentService

    @BeforeEach
    fun init() {
        mockNewStudentDto = NewStudentDto(FIRSTNAME, LASTNAME, EMAIL)
        mockStudentDto =
            StudentDto(
                1,
                mockNewStudentDto.firstname,
                mockNewStudentDto.lastname,
                mockNewStudentDto.email,
                mutableSetOf()
            )
        mockCourseDto =
            CourseDto(
                1,
                "course name",
                mutableSetOf()
            )
    }

    @Test
    fun testCreateStudent() {
        `when`(studentService.saveStudent(mockNewStudentDto)).thenReturn(mockStudentDto)

        mockMvc.perform(
            post("/api/v1/student/create")
                .accept("application/json")
                .contentType("application/json")
                .content(json(mockNewStudentDto))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json(json(mockStudentDto)))
    }

    @Test
    fun testCreateStudentException() {
        val studentAlreadyExistsException =
            StudentAlreadyExistsException("On create student it was found with the e-mail $EMAIL")
        `when`(studentService.saveStudent(mockNewStudentDto))
            .thenThrow(studentAlreadyExistsException)

        mockMvc.perform(
            post("/api/v1/student/create")
                .contentType("application/json;charset=UTF-8")
                .content(json(mockNewStudentDto))
        )
            .andExpect(status().is4xxClientError)
            .andExpect(status().isBadRequest)
            .andExpect(content().string(errorMessage(studentAlreadyExistsException)))
    }

    @Test
    fun testFindStudent() {
        `when`(studentService.findStudent(mockStudentDto)).thenReturn(mockStudentDto)

        mockMvc.perform(
            get("/api/v1/student/find")
                .contentType("application/json;charset=UTF-8")
                .content(json(mockStudentDto))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json(json(mockStudentDto)))
    }

    @Test
    fun testFindStudentStudentNotFoundException() {
        val studentNotFoundException = StudentNotFoundException("StudentDto not found of searching its e-mail address.")
        `when`(studentService.findStudent(mockStudentDto))
            .thenThrow(studentNotFoundException)

        mockMvc.perform(
            get("/api/v1/student/find")
                .contentType("application/json;charset=UTF-8")
                .content(json(mockStudentDto))
        )
            .andExpect(status().is4xxClientError)
            .andExpect(status().isBadRequest)
            .andExpect(content().string(errorMessage(studentNotFoundException)))
    }

    @Test
    fun testFindByEmail() {
        `when`(studentService.findByEmail(EMAIL)).thenReturn(mockStudentDto)

        mockMvc.perform(
            get("/api/v1/student/findByEmail?email=$EMAIL")
                .contentType("application/json;charset=UTF-8")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json(json(mockStudentDto)))
    }

    @ParameterizedTest
    @MethodSource("invalidEmailUseCases")
    fun testFindByInvalidEmailException(invalidEmailUseCases: String) {
        var constraintViolationException = mock(ConstraintViolationException::class.java)
        var email = ""
        when (invalidEmailUseCases) {
            "Email is empty" -> {
                constraintViolationException = ConstraintViolationException(
                    "findByEmail.email: Email is mandatory",
                    setOf()
                )
                email = ""
            }

            "Email is invalid" -> {
                constraintViolationException = ConstraintViolationException(
                    "findByEmail.email: must be a well-formed email address",
                    setOf()
                )
                email = "a"
            }
        }

        when (invalidEmailUseCases) {
            "Email does not exists" -> {
                mockMvc.perform(
                    get("/api/v1/student/findByEmail").contentType("application/json;charset=UTF-8")
                )
                    .andExpect(status().is4xxClientError)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().string(""))
            }

            else -> {
                mockMvc.perform(
                    get("/api/v1/student/findByEmail?email=$email").contentType("application/json;charset=UTF-8")
                )
                    .andExpect(status().is4xxClientError)
                    .andExpect(status().isBadRequest)
                    .andExpect(content().string(errorMessage(constraintViolationException)))
            }
        }
    }

    @Suppress("unused")
    private fun invalidEmailUseCases(): Stream<Arguments> =
        Stream.of(
            Arguments.of("Email does not exists"),
            Arguments.of("Email is empty"),
            Arguments.of("Email is invalid")
        )

    @Test
    fun testFindByEmailException() {
        val studentNotFoundException = StudentNotFoundException("StudentDto not found of searching its e-mail address.")
        `when`(studentService.findByEmail(EMAIL))
            .thenThrow(studentNotFoundException)

        mockMvc.perform(
            get("/api/v1/student/findByEmail?email=$EMAIL")
                .contentType("application/json;charset=UTF-8")
        )
            .andExpect(status().is4xxClientError)
            .andExpect(status().isBadRequest)
            .andExpect(content().string(errorMessage(studentNotFoundException)))
    }

    @Test
    fun testUpdate() {
        `when`(studentService.update(mockStudentDto)).thenReturn(mockStudentDto)

        mockMvc.perform(
            put("/api/v1/student/update")
                .contentType("application/json;charset=UTF-8")
                .content(json(mockStudentDto))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json(json(mockStudentDto)))
    }

    @Test
    fun testRelateToCourses() {
        `when`(studentService.checkAndRelateToCourses(mockStudentDto, setOf(1, 2, 3))).thenReturn(mockStudentDto)

        mockMvc.perform(
            put("/api/v1/student/relateToCourses?courseIds=1,2,3")
                .contentType("application/json;charset=UTF-8")
                .content(json(mockStudentDto))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json(json(mockStudentDto)))
    }

    @ParameterizedTest
    @MethodSource("exceptionCases")
    fun testRelateToCoursesExceptions(useCase: String) {
        val courseNotFoundException: CourseNotFoundException
        val entityAlreadyRelatedException: EntityAlreadyRelatedException
        val studentNotFoundException: StudentNotFoundException
        val errorMessage: String

        val invokeCheckAndRelate = `when`(studentService.checkAndRelateToCourses(mockStudentDto, setOf(1, 2, 3)))
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
            put("/api/v1/student/relateToCourses?courseIds=1,2,3")
                .contentType("application/json;charset=UTF-8")
                .content(json(mockStudentDto))
        )
            .andExpect(status().is4xxClientError)
            .andExpect(status().isBadRequest)
            .andExpect(content().string(errorMessage))
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
        doNothing().`when`(studentService).delete(mockStudentDto)

        mockMvc.perform(
            delete("/api/v1/student/delete")
                .contentType("application/json;charset=UTF-8")
                .content(json(mockStudentDto))
        )
            .andExpect(status().isOk)
    }

    @Test
    fun testDeleteByEmail() {
        doNothing().`when`(studentService).deleteByEmail(mockStudentDto.email)

        mockMvc.perform(
            delete("/api/v1/student/deleteByEmail")
                .contentType("application/json;charset=UTF-8")
                .content(json(mockStudentDto))
        )
            .andExpect(status().isOk)
    }

    private fun errorMessage(exception: Exception) = controllerExceptionHandler.errorMessage(exception)

    private fun json(any: Any): String =
        objectMapper.writeValueAsString(any)

    private val objectMapper = ObjectMapper()
}
