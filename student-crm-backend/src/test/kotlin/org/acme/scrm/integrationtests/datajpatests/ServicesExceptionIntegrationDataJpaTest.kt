package org.acme.scrm.integrationtests.datajpatests

import org.acme.scrm.exception.CourseAlreadyExistsException
import org.acme.scrm.exception.CourseNotFoundException
import org.acme.scrm.exception.StudentAlreadyExistsException
import org.acme.scrm.exception.StudentNotFoundException
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
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServicesExceptionIntegrationDataJpaTest : PostgresTestContainer {

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

    private val emailJohnDoe = "john.doe@uni.edu"
    private val courseCS = "Computer Science"

    @Test
    fun testServicesExceptionOnCreate() {
        studentService.saveStudent(NewStudentDto("John", "Doe", emailJohnDoe))
        assertThrows<StudentAlreadyExistsException> {
            // same email
            studentService.saveStudent(NewStudentDto("Johnny", "Doey", emailJohnDoe))
        }
        courseService.saveCourse(NewCourseDto(courseCS))
        assertThrows<CourseAlreadyExistsException> {
            // same name
            courseService.saveCourse(NewCourseDto(courseCS))
        }
    }

    @Test
    fun testServicesExceptionOnUpdateSimple() {
        val student = studentService.saveStudent(NewStudentDto("John", "Doe", emailJohnDoe))
        val course = courseService.saveCourse(NewCourseDto(courseCS))
        studentService.delete(student)
        courseService.delete(course)

        // On update / simple - Entity not found
        assertThrows<StudentNotFoundException> {
            studentService.update(student)
        }
        assertThrows<CourseNotFoundException> {
            courseService.update(course)
        }

        // On update / relate - Entity not found
        assertThrows<CourseNotFoundException> {
            studentService.checkAndRelateToCourses(student, setOf(course.id))
        }
        assertThrows<StudentNotFoundException> {
            courseService.checkAndRelateToStudent(course, setOf(student.id))
        }
    }

    @Test
    fun testServicesExceptionOnUpdateRelate() {
        var student = studentService.saveStudent(NewStudentDto("John", "Doe", emailJohnDoe))
        var course = courseService.saveCourse(NewCourseDto(courseCS))
        studentService.delete(student)
        courseService.delete(course)

        // On update / relate - Entity not found
        courseService.saveCourse(NewCourseDto(courseCS))
        course = courseService.findByName(courseCS)
        assertThrows<StudentNotFoundException> {
            studentService.checkAndRelateToCourses(student, setOf(course.id))
        }
        courseService.delete(course)

        studentService.saveStudent(NewStudentDto("John", "Doe", emailJohnDoe))
        student = studentService.findByEmail(emailJohnDoe)
        assertThrows<CourseNotFoundException> {
            courseService.checkAndRelateToStudent(course, setOf(student.id))
        }
    }

    @Test
    fun testServicesExceptionOnUpdate() {
        studentService.saveStudent(NewStudentDto("John", "Doe", emailJohnDoe))
        val course = courseService.saveCourse(NewCourseDto(courseCS))
        courseService.delete(course)

        // On update add existent entity
        val emailJonnyDoey = "jonny.doey@uni.edu"
        studentService.saveStudent(NewStudentDto("Johnny", "Doey", emailJonnyDoey))
        val tempStudentJyDy = studentService.findByEmail(emailJonnyDoey)
        assertThrows<StudentAlreadyExistsException> {
            tempStudentJyDy.email = emailJohnDoe
            studentService.update(tempStudentJyDy)
        }
        val tempStudentJD = studentService.findByEmail(emailJohnDoe)
        tempStudentJD.email = "j.dt@uni.edu"
        studentService.update(tempStudentJD)
        tempStudentJyDy.email = emailJohnDoe
        studentService.update(tempStudentJyDy)
        assertEquals(tempStudentJyDy.id, studentService.findByEmail(emailJohnDoe).id)

        val tempCourseCS = courseService.saveCourse(NewCourseDto(courseCS))
        val courseMath = "Math"
        val tempCourseM = courseService.saveCourse(NewCourseDto(courseMath))
        assertThrows<CourseAlreadyExistsException> {
            tempCourseM.name = courseCS
            courseService.update(tempCourseM)
        }
        tempCourseCS.name = "Chem"
        courseService.update(tempCourseCS)
        tempCourseM.name = courseCS
        courseService.update(tempCourseM)
        assertEquals(tempCourseM.id, courseService.findByName(courseCS).id)
    }

    @Test
    fun testServicesExceptionOnDelete() {
        val student = studentService.saveStudent(NewStudentDto("John", "Doe", emailJohnDoe))
        studentService.delete(student)
        val course = courseService.saveCourse(NewCourseDto(courseCS))
        courseService.delete(course)

        assertThrows<StudentNotFoundException> {
            studentService.delete(student)
        }
        assertThrows<CourseNotFoundException> {
            courseService.delete(course)
        }
    }
}
