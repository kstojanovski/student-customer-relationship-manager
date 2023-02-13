package org.acme.scrm.integrationtests.restdocs

import com.fasterxml.jackson.databind.ObjectMapper
import org.acme.scrm.integrationtests.PostgresTestContainer
import org.acme.scrm.persistence.repository.CourseRepository
import org.acme.scrm.persistence.repository.StudentRepository
import org.acme.scrm.service.dto.NewStudentDto
import org.acme.scrm.service.dto.StudentDto
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class StudentControllerRestDocumentTest(@Autowired val mockMvc: MockMvc) : PostgresTestContainer {

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @BeforeEach
    fun prepare() {
        studentRepository.deleteAll()
        courseRepository.deleteAll()
    }

    @Test
    fun testCreateStudent() {
        val firstname = "John"
        val lastname = "Doe"
        val email = "john.doe@uni.edu"
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
            .andDo(
                document(
                    "api-v1-student-create",
                    requestFields(
                        fieldWithPath("firstname").description("The firstname of the student."),
                        fieldWithPath("lastname").description("The lastname of the student."),
                        fieldWithPath("email").description("The email of the student.")
                    )
                )
            )
    }

    @Test
    fun testReadStudent() {
        val firstname = "John"
        val lastname = "Doe"
        val email = "john.doe@uni.edu"
        val newStudentDto = NewStudentDto(firstname, lastname, email)
        val mvcResult = mockMvc.perform(
            post("/api/v1/student/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newStudentDto))
        ).andReturn()
        val createResponsePayload = mvcResult.response.contentAsString
        mockMvc.perform(
            get("/api/v1/student/find")
                .accept("application/json")
                .contentType("application/json")
                .content(createResponsePayload)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.firstname", `is`(firstname)))
            .andExpect(jsonPath("$.lastname", `is`(lastname)))
            .andExpect(jsonPath("$.email", `is`(email)))
            .andDo(
                document(
                    "api-v1-student-read",
                    requestFields(
                        fieldWithPath("id").description("The id of the student."),
                        fieldWithPath("firstname").description("The firstname of the student."),
                        fieldWithPath("lastname").description("The lastname of the student."),
                        fieldWithPath("email").description("The email of the student."),
                        fieldWithPath("courses").description("The courses visited by the student.")
                    )
                )
            )
    }

    @Test
    fun testUpdateStudent() {
        val firstname = "John"
        val lastname = "Doe"
        val email = "john.doe@uni.edu"
        val newStudentDto = NewStudentDto(firstname, lastname, email)
        val mvcResult = mockMvc.perform(
            post("/api/v1/student/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newStudentDto))
        ).andReturn()
        val createResponsePayload = mvcResult.response.contentAsString
        mockMvc.perform(
            put("/api/v1/student/update")
                .accept("application/json")
                .contentType("application/json")
                .content(createResponsePayload)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.firstname", `is`(firstname)))
            .andExpect(jsonPath("$.lastname", `is`(lastname)))
            .andExpect(jsonPath("$.email", `is`(email)))
            .andDo(
                document(
                    "api-v1-student-update",
                    requestFields(
                        fieldWithPath("id").description("The id of the student."),
                        fieldWithPath("firstname").description("The firstname of the student."),
                        fieldWithPath("lastname").description("The lastname of the student."),
                        fieldWithPath("email").description("The email of the student."),
                        fieldWithPath("courses").description("The courses visited by the student.")
                    )
                )
            )
    }

    @Test
    fun testDeleteStudent() {
        val firstname = "John"
        val lastname = "Doe"
        val email = "john.doe@uni.edu"
        val newStudentDto = NewStudentDto(firstname, lastname, email)
        mockMvc.perform(
            post("/api/v1/student/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newStudentDto))
        )

        val studentDto = StudentDto(1, firstname, lastname, email, mutableSetOf())
        mockMvc.perform(
            delete("/api/v1/student/delete")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(studentDto))
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "api-v1-student-delete",
                    requestFields(
                        fieldWithPath("id").description("The id of the student."),
                        fieldWithPath("firstname").description("The firstname of the student."),
                        fieldWithPath("lastname").description("The lastname of the student."),
                        fieldWithPath("email").description("The email of the student."),
                        fieldWithPath("courses").description("The courses visited by the student.")
                    )
                )
            )
    }

    private fun objToJson(any: Any): String = objectMapper.writeValueAsString(any)

    private val objectMapper = ObjectMapper()
}
