package org.acme.scrm.integrationtests.restdocs

import com.fasterxml.jackson.databind.ObjectMapper
import org.acme.scrm.integrationtests.PostgresTestContainer
import org.acme.scrm.persistence.repository.CourseRepository
import org.acme.scrm.persistence.repository.StudentRepository
import org.acme.scrm.service.dto.CourseDto
import org.acme.scrm.service.dto.NewCourseDto
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
class CourseControllerRestDocumentTest(@Autowired val mockMvc: MockMvc) : PostgresTestContainer {

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
    fun testCreateCourse() {
        val name = "Computer Science"
        val newCourseDto = NewCourseDto(name)
        mockMvc.perform(
            post("/api/v1/course/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newCourseDto))
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.name", `is`(name)))
            .andDo(
                document(
                    "api-v1-course-create",
                    requestFields(
                        fieldWithPath("name").description("The name of the course.")
                    )
                )
            )
    }

    @Test
    fun testReadCourse() {
        val name = "Computer Science"
        val newCourseDto = NewCourseDto(name)
        val mvcResult = mockMvc.perform(
            post("/api/v1/course/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newCourseDto))
        ).andReturn()
        val createResponsePayload = mvcResult.response.contentAsString
        mockMvc.perform(
            get("/api/v1/course/find")
                .accept("application/json")
                .contentType("application/json")
                .content(createResponsePayload)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.name", `is`(name)))
            .andDo(
                document(
                    "api-v1-course-read",
                    requestFields(
                        fieldWithPath("id").description("The id of the course."),
                        fieldWithPath("name").description("The name of the course."),
                        fieldWithPath("students").description("The students visiting the course.")
                    )
                )
            )
    }

    @Test
    fun testUpdateCourse() {
        val name = "Computer Science"
        val newCourseDto = NewCourseDto(name)
        val mvcResult = mockMvc.perform(
            post("/api/v1/course/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newCourseDto))
        ).andReturn()
        val createResponsePayload = mvcResult.response.contentAsString
        mockMvc.perform(
            put("/api/v1/course/update")
                .accept("application/json")
                .contentType("application/json")
                .content(createResponsePayload)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType("application/json"))
            .andExpect(jsonPath("$.name", `is`(name)))
            .andDo(
                document(
                    "api-v1-course-update",
                    requestFields(
                        fieldWithPath("id").description("The id of the course."),
                        fieldWithPath("name").description("The name of the course."),
                        fieldWithPath("students").description("The students visiting the course.")
                    )
                )
            )
    }

    @Test
    fun testDeleteCourse() {
        val name = "Computer Science"
        val newCourseDto = NewCourseDto(name)
        mockMvc.perform(
            post("/api/v1/course/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newCourseDto))
        )
        val courseDto = CourseDto(1, name, mutableSetOf())
        mockMvc.perform(
            delete("/api/v1/course/delete")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(courseDto))
        )
            .andExpect(status().isOk)
            .andDo(
                document(
                    "api-v1-course-delete",
                    requestFields(
                        fieldWithPath("id").description("The id of the course."),
                        fieldWithPath("name").description("The name of the course."),
                        fieldWithPath("students").description("The students visiting the course.")
                    )
                )
            )
    }

    private fun objToJson(any: Any): String = objectMapper.writeValueAsString(any)

    private val objectMapper = ObjectMapper()
}
