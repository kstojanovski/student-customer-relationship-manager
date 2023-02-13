package org.acme.scrm.integrationtests.datajpatests

import org.acme.scrm.integrationtests.PostgresTestContainer
import org.acme.scrm.persistence.model.Course
import org.acme.scrm.persistence.model.Student
import org.acme.scrm.persistence.repository.CourseRepository
import org.acme.scrm.persistence.repository.StudentRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryReadIntegrationDataJpaTest : PostgresTestContainer {

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
    fun testFindFromRepositories() {
        // Arrange
        val students = setOf(
            Student().apply { firstname = "John"; lastname = "Wick"; email = "john.wick@pacifists.com" },
            Student().apply { firstname = "Mad"; lastname = "Max"; email = "mad.max@pacifists.com" },
            Student().apply { firstname = "John"; lastname = "Rambo"; email = "john.rambo@pacifists.com" },
            Student().apply { firstname = "John"; lastname = "Wayne"; email = "john.wayne@pacifists.com" },
            Student().apply { firstname = "Dirty"; lastname = "Harry"; email = "dirty.harry@pacifists.com" },
            Student().apply { firstname = "Harry"; lastname = "Potter"; email = "harry.potter@pacifists.com" },
            Student().apply { firstname = "John"; lastname = "Doe"; email = "john.doe@pacifists.com" }
        )
        studentRepository.saveAll(students)
        val courses = mutableSetOf(
            Course().apply { name = "Pacifism" },
            Course().apply { name = "Tolerance" },
            Course().apply { name = "Behaviour" },
            Course().apply { name = "Veganism" }
        )
        courseRepository.saveAll(courses)

        relate("john.wick@pacifists.com", "Pacifism")
        relate("john.wick@pacifists.com", "Veganism")

        relate("mad.max@pacifists.com", "Veganism")

        relate("john.rambo@pacifists.com", "Pacifism")
        relate("john.rambo@pacifists.com", "Tolerance")
        relate("john.rambo@pacifists.com", "Behaviour")
        relate("john.rambo@pacifists.com", "Veganism")

        relate("dirty.harry@pacifists.com", "Behaviour")
        relate("dirty.harry@pacifists.com", "Tolerance")

        // Act and Assert
        var foundStudents = studentRepository.findByCourseName("Pacifism")
        Assertions.assertEquals(2, foundStudents.toMutableSet().size)
        foundStudents = studentRepository.findByCourseName("Veganism")
        Assertions.assertEquals(3, foundStudents.toMutableSet().size)

        Assertions.assertEquals(1, studentRepository.findByName("John", "Doe")!!.size)

        val foundCourses = courseRepository.findByStudentEmail("john.rambo@pacifists.com")
        Assertions.assertEquals(4, foundCourses!!.toMutableSet().size)

        val actualStudent = studentRepository.findByEmail("john.rambo@pacifists.com")
        Assertions.assertEquals("John", actualStudent!!.firstname)
        Assertions.assertEquals("Rambo", actualStudent.lastname)
    }

    private fun relate(email: String, courseName: String) {
        val student = studentRepository.findByEmail(email)
        val course = courseRepository.findByName(courseName)
        if (student != null && course != null) {
            student.courses.add(course)
            studentRepository.save(student)
        }
    }
}
