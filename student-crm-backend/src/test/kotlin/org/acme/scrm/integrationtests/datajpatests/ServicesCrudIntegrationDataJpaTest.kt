package org.acme.scrm.integrationtests.datajpatests

import org.acme.scrm.integrationtests.PostgresTestContainer
import org.acme.scrm.persistence.repository.CourseRepository
import org.acme.scrm.persistence.repository.StudentRepository
import org.acme.scrm.service.CourseService
import org.acme.scrm.service.StudentService
import org.acme.scrm.service.dto.NewCourseDto
import org.acme.scrm.service.dto.NewStudentDto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServicesCrudIntegrationDataJpaTest : PostgresTestContainer {

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Autowired
    private lateinit var courseRepository: CourseRepository

    private lateinit var studentService: StudentService
    private lateinit var courseService: CourseService

    @BeforeEach
    fun prepare() {
        studentRepository.deleteAll()
        courseRepository.deleteAll()
        studentService = StudentService(studentRepository, courseRepository)
        courseService = CourseService(courseRepository, studentRepository)
    }

    @Test
    fun testServicesCrud() {
        val courseCS = "Computer Science"
        val courseChem = "Chemistry"
        val courseMath = "Mathematics"
        val emailJohnDoe = "john.doe@uni.edu"
        val emailJaneDoe = "jane.doe@uni.edu"
        val emailMaxMuster = "max.muster@uni.edu"

        // Create Service

        val studentService = StudentService(studentRepository, courseRepository)
        val courseService = CourseService(courseRepository, studentRepository)

        // Create Data

        studentService.saveStudent(NewStudentDto("John", "Doe", emailJohnDoe))
        studentService.saveStudent(NewStudentDto("Jane", "Doe", emailJaneDoe))
        courseService.saveCourse(NewCourseDto(courseCS))
        courseService.saveCourse(NewCourseDto(courseMath))

        // Relate

        studentService.checkAndRelateToCourses(
            studentService.findByEmail(emailJohnDoe),
            setOf(courseService.findByName(courseCS).id, courseService.findByName(courseMath).id)
        )
        studentService.checkAndRelateToCourses(
            studentService.findByEmail(emailJaneDoe),
            setOf(courseService.findByName(courseCS).id, courseService.findByName(courseMath).id)
        )
        assertEquals(2, studentService.findByEmail(emailJohnDoe).courses.size)
        assertEquals(2, studentService.findByEmail(emailJaneDoe).courses.size)

        // Update

        val tempCourse = courseService.findByName(courseCS)
        tempCourse.name = courseChem
        courseService.update(tempCourse)

        val tempStudent = studentService.findByEmail(emailJohnDoe)
        tempStudent.firstname = "Johnny"
        studentService.update(tempStudent)

        // New case, new course and student

        courseService.saveCourse(NewCourseDto(courseCS))
        studentService.saveStudent(NewStudentDto("Max", "Muster", emailMaxMuster))
        studentService.checkAndRelateToCourses(
            studentService.findByEmail(emailMaxMuster),
            setOf(courseService.findByName(courseCS).id, courseService.findByName(courseMath).id)
        )

        assertEquals(3, studentService.findByCourseName(courseMath).toMutableSet().size)
        assertEquals(1, studentService.findByCourseName(courseCS).toMutableSet().size)
        assertEquals(2, studentService.findByCourseName(courseChem).toMutableSet().size)

        // Deletion
        courseService.deleteByName(courseMath)
        assertEquals(0, studentService.findByCourseName(courseMath).toMutableSet().size)
        studentService.deleteByEmail(emailJohnDoe)
        assertEquals(1, studentService.findByCourseName(courseCS).toMutableSet().size)
        assertEquals(1, studentService.findByCourseName(courseChem).toMutableSet().size)

        assertEquals(2, courseService.findAllCourses().toMutableSet().size)
        assertEquals(2, studentService.findAllStudents().toMutableSet().size)
    }
}
