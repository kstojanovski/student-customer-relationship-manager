package org.acme.scrm.integrationtests.mvctests

import com.fasterxml.jackson.databind.ObjectMapper
import org.acme.scrm.integrationtests.PostgresTestContainer
import org.acme.scrm.persistence.model.Course
import org.acme.scrm.persistence.model.Student
import org.acme.scrm.persistence.repository.CourseRepository
import org.acme.scrm.persistence.repository.StudentRepository
import org.acme.scrm.service.dto.CourseDto
import org.acme.scrm.service.dto.NewCourseDto
import org.acme.scrm.service.dto.NewStudentDto
import org.acme.scrm.service.dto.StudentDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RelationIntegrationMvcTest(@Autowired val mockMvc: MockMvc) : PostgresTestContainer {

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
    fun `Testing relation between student and course`() {
        val firstname = "John"
        val lastname = "Doe"
        val email = "john.doe@uni.com"
        val newStudentDto = NewStudentDto(firstname, lastname, email)
        var mvcResultStudent = mockMvc.perform(
            post("/api/v1/student/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newStudentDto))
        )
            .andExpect(status().isOk)
            .andReturn()

        val courseName = "Computer Science"
        val newCourseDto = NewCourseDto(courseName)
        val mvcResultCourse = mockMvc.perform(
            post("/api/v1/course/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newCourseDto))
        )
            .andExpect(status().isOk)
            .andReturn()

        var student = jsonToStudent(mvcResultStudent.response.contentAsString).toStudentDto()
        var course = jsonToCourse(mvcResultCourse.response.contentAsString).toCourseDto()

        val studentDto = StudentDto(student.id, firstname, lastname, email, mutableSetOf())
        mvcResultStudent = mockMvc.perform(
            put("/api/v1/student/relateToCourses?courseIds=${course.id}")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(studentDto))
        )
            .andExpect(status().isOk)
            .andReturn()

        student = jsonToStudent(mvcResultStudent.response.contentAsString).toStudentDto()
        assertEquals(1, student.courses.size)
        assertEquals(firstname, student.firstname)
        assertEquals(lastname, student.lastname)
        assertEquals(email, student.email)
        course = student.courses.iterator().next()
        assertEquals(courseName, course.name)
        assertEquals(mutableSetOf<StudentDto>(), course.students)
    }

    @Test
    fun `Testing relation between course and student`() {
        val firstname = "John"
        val lastname = "Doe"
        val email = "john.doe@uni.com"
        val newStudentDto = NewStudentDto(firstname, lastname, email)
        val mvcResultStudent = mockMvc.perform(
            post("/api/v1/student/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newStudentDto))
        )
            .andExpect(status().isOk)
            .andReturn()

        val courseName = "Computer Science"
        val newCourseDto = NewCourseDto(courseName)
        var mvcResultCourse = mockMvc.perform(
            post("/api/v1/course/create")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(newCourseDto))
        )
            .andExpect(status().isOk)
            .andReturn()

        var student = jsonToStudent(mvcResultStudent.response.contentAsString).toStudentDto()
        var course = jsonToCourse(mvcResultCourse.response.contentAsString).toCourseDto()

        val courseDto = CourseDto(course.id, courseName, mutableSetOf())
        mvcResultCourse = mockMvc.perform(
            put("/api/v1/course/relateToStudent?studentIds=${student.id}")
                .accept("application/json")
                .contentType("application/json")
                .content(objToJson(courseDto))
        )
            .andExpect(status().isOk)
            .andReturn()

        course = jsonToCourse(mvcResultCourse.response.contentAsString).toCourseDto()
        assertEquals(1, course.students.size)
        assertEquals(courseName, course.name)
        student = course.students.iterator().next()
        assertEquals(firstname, student.firstname)
        assertEquals(lastname, student.lastname)
        assertEquals(email, student.email)
        assertEquals(mutableSetOf<CourseDto>(), student.courses)
    }

    private fun objToJson(any: Any): String = objectMapper.writeValueAsString(any)

    private fun jsonToStudent(serializedStudent: String): Student =
        objectMapper.readValue(serializedStudent.toByteArray(Charsets.UTF_8), Student::class.java)

    private fun jsonToCourse(serializedStudent: String): Course =
        objectMapper.readValue(serializedStudent.toByteArray(Charsets.UTF_8), Course::class.java)

    private val objectMapper = ObjectMapper()
}
