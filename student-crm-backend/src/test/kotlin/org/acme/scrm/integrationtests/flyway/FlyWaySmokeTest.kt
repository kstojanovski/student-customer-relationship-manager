package org.acme.scrm.integrationtests.flyway

import org.acme.scrm.integrationtests.FlywayPostgresTestContainer
import org.acme.scrm.persistence.model.Course
import org.acme.scrm.persistence.model.Student
import org.acme.scrm.persistence.repository.CourseRepository
import org.acme.scrm.persistence.repository.StudentRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class FlyWaySmokeTest : FlywayPostgresTestContainer {

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
    fun testSmoke() {
        val email = "f.l@uni.com"
        val student = studentRepository.save(
            Student().apply { this.firstname = "f"; this.lastname = "l"; this.email = email }
        )
        assertEquals(email, student.email)

        val courseName = "CS"
        val course = courseRepository.save(
            Course().apply { this.name = courseName }
        )
        assertEquals(courseName, course.name)

        student.courses.add(course)
        studentRepository.save(student)

        assertEquals(1, courseRepository.findByStudentEmail(email)!!.toMutableSet().size)
    }
}
