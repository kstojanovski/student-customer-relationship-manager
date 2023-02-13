package org.acme.scrm.integrationtests.datajpatests

import org.acme.scrm.integrationtests.PostgresTestContainer
import org.acme.scrm.persistence.model.Student
import org.acme.scrm.persistence.repository.StudentRepository
import org.acme.scrm.persistence.repository.StudentsPagedRepository
import org.acme.scrm.persistence.repository.StudentsPagedRepositoryImp
import org.acme.scrm.service.StudentsPagedService
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import javax.validation.ConstraintViolationException

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudentIntegrationDataJpaTest : PostgresTestContainer {

    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Autowired
    private lateinit var studentsPagedRepository: StudentsPagedRepository
    private lateinit var studentsPagedRepositoryImp: StudentsPagedRepositoryImp

    @BeforeEach
    fun `clean up the dbs`() {
        studentRepository.deleteAll()
        studentsPagedRepositoryImp = StudentsPagedRepositoryImp(studentsPagedRepository)
    }

    @Test
    fun `when record is saved then the id is populated`() {
        studentRepository.save(
            Student().apply {
                firstname = "John"; lastname = "Wick"; email = "john.wick@pacifists.com"
            }
        )
        assertEquals(1, studentRepository.findAll().toMutableSet().size)
    }

    /**
     * Explaining the situation here is that the constraints violation exception is triggered on flushing the data into
     * the DB which is not done on the save-command but before findByEmail.
     */
    @Test
    fun `if the email is null then an exception is thrown`() {
        // Arrange
        val actualStudent = Student().apply { firstname = "John"; lastname = "Wick" }
        studentRepository.save(actualStudent)
        // Act and Assert
        assertThrows(
            ConstraintViolationException::class.java
        ) { studentRepository.findByEmail("") }
    }

    /**
     * Explaining the situation here is that the constraints violation exception is triggered on flushing the data into
     * the DB which is not done on the save-command but before findByEmail.
     */
    @Test
    fun `there is no protection of invalid email on save, but on get - not good`() {
        val student = Student().apply { firstname = "John"; lastname = "Wick"; email = "" }
        studentRepository.save(student)
        assertThrows(
            ConstraintViolationException::class.java
        ) { studentRepository.findByEmail("") }
    }

    /**
     * Explaining the situation here is that the constraints violation exception is triggered on flushing the data into
     * the DB which is not done on the save-command but before findByEmail.
     */
    @Test
    fun `there is no protection of invalid email on save`() {
        val savedStudent = studentRepository.save(Student().apply { firstname = "John"; lastname = "Wick"; email = "" })
        assertEquals("John", savedStudent.firstname)
        assertEquals("Wick", savedStudent.lastname)
        assertEquals("", savedStudent.email)
        assertEquals(0, savedStudent.courses.size)
        assertThrows(
            ConstraintViolationException::class.java
        ) { studentRepository.findByEmail("") }
    }

    @Test
    fun `when multiple records with the same preferred name then all are found`() {
        // Arrange
        val studentsPagedService = StudentsPagedService(studentsPagedRepositoryImp)
        val students = mutableSetOf(
            Student().apply { firstname = "John"; lastname = "Wick"; email = "john.wick@pacifists.com" },
            Student().apply { firstname = "John"; lastname = "Doe"; email = "john.doe@pacifists.com" }
        )
        studentRepository.saveAll(students)
        // Act
        val actualStudents = studentsPagedService.findByFirstnameContaining("John", 0, Int.MAX_VALUE)
        // Assert
        assertEquals(2, actualStudents.size)
    }

    @Test
    fun `when multiple records with the same email then the only the first is found`() {
        // Arrange
        val studentsPagedService = StudentsPagedService(studentsPagedRepositoryImp)
        val students = mutableSetOf(
            Student().apply { firstname = "John"; lastname = "Wick"; email = "john.wick@pacifists.com" },
            Student().apply { firstname = "John"; lastname = "Doe"; email = "john.wick@pacifists.com" }
        )
        studentRepository.saveAll(students)
        // Act
        val actualStudents = studentsPagedService.findByFirstnameContaining("John", 0, Int.MAX_VALUE)
        // Assert
        assertEquals(1, actualStudents.size)
        val actualStudent = actualStudents.iterator().next()
        assertEquals("John", actualStudent.firstname)
        assertEquals("Wick", actualStudent.lastname)
    }
}
