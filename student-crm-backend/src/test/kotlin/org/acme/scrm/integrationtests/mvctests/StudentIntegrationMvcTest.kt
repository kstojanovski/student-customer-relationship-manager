package org.acme.scrm.integrationtests.mvctests

import com.fasterxml.jackson.databind.ObjectMapper
import org.acme.scrm.integrationtests.PostgresTestContainer
import org.acme.scrm.persistence.model.Student
import org.acme.scrm.persistence.repository.CourseRepository
import org.acme.scrm.persistence.repository.StudentRepository
import org.acme.scrm.service.dto.NewStudentDto
import org.acme.scrm.service.dto.StudentDto
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.provider.Arguments
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.stream.Stream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class StudentIntegrationMvcTest(@Autowired val mockMvc: MockMvc) : PostgresTestContainer {

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @BeforeEach
    fun prepare() {
        studentRepository.deleteAll()
        courseRepository.deleteAll()
    }

    @Suppress("unused")
    private fun newStudentDtoValidationUseCases(): Stream<Arguments> =
        Stream.of(
            Arguments.of("Email is empty"),
            Arguments.of("Email has invalid format"),
            Arguments.of("Firstname is empty"),
            Arguments.of("Firstname has more then 20 characters"),
            Arguments.of("Lastname is empty"),
            Arguments.of("Lastname has more then 20 characters")
        )

    @Test
    fun `Testing new student DTO validation with invalid inputs on create student`() {
        val firstname = "John"
        val lastname = "Doe"
        val email = "john.doe@uni.edu"
        val overTwentyChars = "IamOverTwentyCharactersLong"
        newStudentDtoValidationOnCreate(firstname, lastname, email.substring(0, 0))
        newStudentDtoValidationOnCreate(firstname, lastname, email.replace("@", "[at]"))
        newStudentDtoValidationOnCreate(firstname.substring(0, 0), lastname, email)
        newStudentDtoValidationOnCreate(firstname.substring(0, 0).plus(overTwentyChars), lastname, email)
        newStudentDtoValidationOnCreate(firstname, lastname.substring(0, 0), email)
        newStudentDtoValidationOnCreate(firstname, lastname.substring(0, 0).plus(overTwentyChars), email)
    }

    private fun newStudentDtoValidationOnCreate(firstname: String, lastname: String, email: String) {
        val newStudentDto = NewStudentDto(firstname, lastname, email)
        mockMvc.perform(
            post("/api/v1/student/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newStudentDto))
        )
            .andExpect(status().isBadRequest)
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun `Testing student DTO validation with invalid inputs on update student`() {
        val firstname = "John"
        val lastname = "Doe"
        val email = "john.doe@uni.com"
        val newStudentDto = NewStudentDto(firstname, lastname, email)
        val mvcResult = mockMvc.perform(
            post("/api/v1/student/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newStudentDto))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.firstname", `is`(firstname)))
            .andExpect(jsonPath("$.lastname", `is`(lastname)))
            .andExpect(jsonPath("$.email", `is`(email)))
            .andReturn()
        val serializedStudentDto = mvcResult.response.contentAsString
        val overTwentyChars = "IamOverTwentyCharactersLong"

        studentDtoValidationOnUpdate(
            jsonToStudent(serializedStudentDto).toStudentDto(),
            firstname,
            lastname,
            email.substring(0, 0)
        )
        studentDtoValidationOnUpdate(
            jsonToStudent(serializedStudentDto).toStudentDto(),
            firstname,
            lastname,
            email.replace("@", "[at]")
        )
        studentDtoValidationOnUpdate(
            jsonToStudent(serializedStudentDto).toStudentDto(),
            firstname.substring(0, 0),
            lastname,
            email
        )
        studentDtoValidationOnUpdate(
            jsonToStudent(serializedStudentDto).toStudentDto(),
            firstname.substring(0, 0).plus(overTwentyChars),
            lastname,
            email
        )
        studentDtoValidationOnUpdate(
            jsonToStudent(serializedStudentDto).toStudentDto(),
            firstname,
            lastname.substring(0, 0),
            email
        )
        studentDtoValidationOnUpdate(
            jsonToStudent(serializedStudentDto).toStudentDto(),
            firstname,
            lastname.substring(0, 0).plus(overTwentyChars),
            email
        )
    }

    private fun studentDtoValidationOnUpdate(
        studentDto: StudentDto,
        firstname: String,
        lastname: String,
        email: String
    ) {
        studentDto.firstname = firstname
        studentDto.lastname = lastname
        studentDto.email = email

        mockMvc.perform(
            put("/api/v1/student/update")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(studentDto))
        )
            .andExpect(status().isBadRequest)
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun testWriteAndReadStudentByEmail() {
        val firstname = "John"
        val lastname = "Doe"
        val email = "john.doe@uni.com"
        val newStudentDto = NewStudentDto(firstname, lastname, email)
        mockMvc.perform(
            post("/api/v1/student/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newStudentDto))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.firstname", `is`(firstname)))
            .andExpect(jsonPath("$.lastname", `is`(lastname)))
            .andExpect(jsonPath("$.email", `is`(email)))
        mockMvc.perform(
            get("/api/v1/student/findByEmail?email=$email")
                .contentType("application/json;charset=UTF-8")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.firstname", `is`(firstname)))
            .andExpect(jsonPath("$.lastname", `is`(lastname)))
            .andExpect(jsonPath("$.email", `is`(email)))
    }

    @Test
    fun testGetStudentByEmail() {
        val email = "f.l@uni.com"
        val student = Student().apply { this.firstname = "f"; this.lastname = "l"; this.email = email }
        studentRepository.save(student)

        mockMvc.perform(
            get("/api/v1/student/findByEmail?email=$email")
                .contentType("application/json;charset=UTF-8")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(content().json(objToJson(student)))
    }

    private fun objToJson(any: Any): String = objectMapper.writeValueAsString(any)

    private fun jsonToStudent(serializedStudent: String): Student =
        objectMapper.readValue(serializedStudent.toByteArray(Charsets.UTF_8), Student::class.java)

    private val objectMapper = ObjectMapper()
}
