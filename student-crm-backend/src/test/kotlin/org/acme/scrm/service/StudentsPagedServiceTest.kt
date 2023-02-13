package org.acme.scrm.service

import org.acme.scrm.persistence.model.Student
import org.acme.scrm.persistence.repository.StudentsPagedRepositoryImp
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl

class StudentsPagedServiceTest {

    private var mockStudentsPagedRepositoryImp: StudentsPagedRepositoryImp = mockk()
    private var studentsPagedService: StudentsPagedService = StudentsPagedService(mockStudentsPagedRepositoryImp)

    private val page = 0
    private val size = 5
    private val studentId = 1L
    private val firstname = "John"
    private val lastname = "Wick"
    private val student = Student().apply {
        this.id = this@StudentsPagedServiceTest.studentId
        this.firstname = this@StudentsPagedServiceTest.firstname
        this.lastname = this@StudentsPagedServiceTest.lastname
        this.email = "john.wick@uni.edu"
        this.courses = mutableSetOf()
    }
    private val content = mutableListOf(student)

    @Test
    fun testFindByFirstnameContaining() {
        // Arrange
        every {
            mockStudentsPagedRepositoryImp.findByFirstnameContaining(
                any(),
                any(),
                any()
            )
        } returns PageImpl(content)

        // Act
        studentsPagedService.findByFirstnameContaining(firstname, page, size)

        // Assert
        verify(exactly = 1) { mockStudentsPagedRepositoryImp.findByFirstnameContaining(any(), any(), any()) }
    }

    @Test
    fun testFindByLastnameContaining() {
        // Arrange
        every { mockStudentsPagedRepositoryImp.findByLastnameContaining(any(), any(), any()) } returns PageImpl(content)

        // Act
        studentsPagedService.findByLastnameContaining(lastname, page, size)

        // Assert
        verify(exactly = 1) { mockStudentsPagedRepositoryImp.findByLastnameContaining(any(), any(), any()) }
    }

    @Test
    fun findByFirstnameContainingOrLastnameContaining() {
        // Arrange
        val pages = PageImpl(content)
        every {
            mockStudentsPagedRepositoryImp.findByFirstnameContainingOrLastnameContaining(
                any(),
                any(),
                any(),
                any()
            )
        } returns pages

        // Act
        studentsPagedService.findByFirstnameContainingOrLastnameContaining(firstname, lastname, page, size)

        // Assert
        verify(exactly = 1) {
            mockStudentsPagedRepositoryImp.findByFirstnameContainingOrLastnameContaining(
                any(),
                any(),
                any(),
                any()
            )
        }
    }

    @Test
    fun testFindByCourseName() {
        // Arrange
        every { mockStudentsPagedRepositoryImp.findByCourseName(any(), any(), any()) } returns PageImpl(content)

        // Act
        studentsPagedService.findByCourseName(firstname, page, size)

        // Assert
        verify(exactly = 1) { mockStudentsPagedRepositoryImp.findByCourseName(any(), any(), any()) }
    }

    @Test
    fun findByCourseNameNative() {
        // Arrange
        every { mockStudentsPagedRepositoryImp.findByCourseNameNative(any(), any(), any()) } returns mutableSetOf(
            student
        )

        // Act
        studentsPagedService.findByCourseNameNative(firstname, page, size)

        // Assert
        verify(exactly = 1) { mockStudentsPagedRepositoryImp.findByCourseNameNative(any(), any(), any()) }
    }
}
