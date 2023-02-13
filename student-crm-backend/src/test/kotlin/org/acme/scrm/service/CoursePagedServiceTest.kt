package org.acme.scrm.service

import org.acme.scrm.persistence.model.Course
import org.acme.scrm.persistence.repository.CoursesPagedRepositoryImp
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl

class CoursePagedServiceTest {

    private var mockCoursesPagedRepositoryImpl: CoursesPagedRepositoryImp = mockk()
    private var coursesPagedService: CoursesPagedService = CoursesPagedService(mockCoursesPagedRepositoryImpl)

    private val page = 0
    private val size = 5
    private val courseId = 1L
    private val email = "email"
    private val name = "name"
    private val course = Course().apply {
        this.id = this@CoursePagedServiceTest.courseId
        this.name = this@CoursePagedServiceTest.name
        this.students = mutableSetOf()
    }
    private val content = mutableListOf(course)

    @Test
    fun testFindByCourseName() {
        // Arrange
        every { mockCoursesPagedRepositoryImpl.findByStudentEmail(any(), any(), any()) } returns PageImpl(content)

        // Act
        coursesPagedService.findByStudentEmail(email, page, size)

        // Assert
        verify(exactly = 1) { mockCoursesPagedRepositoryImpl.findByStudentEmail(any(), any(), any()) }
    }

    @Test
    fun findByCourseNameNative() {
        // Arrange
        every { mockCoursesPagedRepositoryImpl.findByStudentEmailNative(any(), any(), any()) } returns setOf(course)

        // Act
        coursesPagedService.findByStudentEmailNative(email, page, size)

        // Assert
        verify(exactly = 1) { mockCoursesPagedRepositoryImpl.findByStudentEmailNative(any(), any(), any()) }
    }
}
