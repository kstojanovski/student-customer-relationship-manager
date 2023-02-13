package org.acme.scrm.service

import org.acme.scrm.exception.CourseAlreadyExistsException
import org.acme.scrm.exception.CourseNotFoundException
import org.acme.scrm.exception.EntityAlreadyRelatedException
import org.acme.scrm.exception.StudentNotFoundException
import org.acme.scrm.persistence.model.Course
import org.acme.scrm.persistence.model.Student
import org.acme.scrm.persistence.repository.CourseRepository
import org.acme.scrm.persistence.repository.StudentRepository
import org.acme.scrm.service.dto.CourseDto
import org.acme.scrm.service.dto.StudentDto
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class CourseServiceTest {

    private lateinit var mockStudentRepository: StudentRepository
    private lateinit var mockCourseRepository: CourseRepository
    private lateinit var courseService: CourseService

    /**
     * When using the "verify"-checker in the parameterized test then the "verify"-checker is counting upwards
     * (independently if the test case is exception or verify) as long the same repository and service instances are
     * used.
     * Therefore, we need to reinitialize the repositories and the service before each parameterized test part is
     * triggered.
     */
    @BeforeEach
    fun init() {
        mockStudentRepository = mockk()
        mockCourseRepository = mockk()
        courseService = CourseService(mockCourseRepository, mockStudentRepository)
    }

    @Test
    fun testSaveCourse() {
        // Arrange
        val courseDto = mockk<CourseDto>(relaxed = true)
        val course = mockk<Course>(relaxed = true)
        every { mockCourseRepository.findByName(any()) } returns null
        every { mockCourseRepository.save(any()) } returns course
        // Act
        courseService.saveCourse(courseDto)
        // Arrange
        verify(exactly = 1) { mockCourseRepository.save(any()) }
    }

    @Test
    fun testSaveCourseCheckIfStudentAlreadyExists() {
        // Arrange
        every { mockCourseRepository.findByName(any()) } returns Course()
        // Act and Assert
        assertThrows(
            CourseAlreadyExistsException::class.java
        ) { courseService.saveCourse(mockk<CourseDto>(relaxed = true)) }
    }

    @ParameterizedTest
    @MethodSource("relateArguments")
    fun testSaveCourseAndRelate(
        useCase: String
    ) {
        // Arrange
        every { mockCourseRepository.findByName(any()) } returns null
        every { mockCourseRepository.save(any()) } returns mockk(relaxed = true)
        when (useCase) {
            "Course is related to the Student" -> {
                every { mockCourseRepository.existsById(any()) } returns true
                every { mockStudentRepository.existsById(any()) } returns true
                every { mockStudentRepository.saveAll(setOf()) } returns setOf()
            }
        }

        when (useCase) {
            "Course is related to the Student" -> {
                // Act
                courseService.saveCourseAndRelate(mockk(relaxed = true), mockk(relaxed = true))
                // Arrange
                verify(exactly = 1) { mockCourseRepository.save(any()) }
                verify(exactly = 1) { mockStudentRepository.saveAll(setOf()) }
            }
        }
    }

    @Suppress("unused")
    private fun relateArguments(): Stream<Arguments> =
        Stream.of(
            Arguments.of("Course is related to the Student"),
            Arguments.of("StudentNotFoundException - The Student does not exits"),
            Arguments.of("EntityAlreadyRelatedException - The Course is already related to the student"),
            Arguments.of("CourseNotFoundException - Course does not exists")
        )

    @ParameterizedTest
    @MethodSource("findByIdArguments")
    fun testFindById(
        useCase: String,
        methodInvokedTimes: Int
    ) {
        // Arrange
        val courseDto = mockk<CourseDto>(relaxed = true)
        when (useCase) {
            "Course found" -> every { mockCourseRepository.findById(any()) } returns Optional.of(mockk(relaxed = true))
            "Course not found" -> every { mockCourseRepository.findById(any()) } returns Optional.empty()
        }

        // Act and Assert
        when (useCase) {
            "Course found" -> {
                val resultCourseDto = courseService.findCourse(courseDto)
                verify(exactly = methodInvokedTimes) { mockCourseRepository.findById(any()) }
                assertNotNull(resultCourseDto)
            }

            "Course not found" -> assertThrows(
                CourseNotFoundException::class.java
            ) { courseService.findCourse(courseDto) }
        }
    }

    @Suppress("unused")
    private fun findByIdArguments(): Stream<Arguments> =
        Stream.of(
            Arguments.of("Course found", 1),
            Arguments.of("Course not found", 1)
        )

    @ParameterizedTest
    @MethodSource("courseArguments")
    fun findByName(
        useCase: String
    ) {
        // Arrange
        val mockCourseDto = mockk<CourseDto>(relaxed = true)
        val mockCourse = mockk<Course>(relaxed = true)
        val courseName = "courseName"
        every { mockCourseDto.name } returns courseName
        when (useCase) {
            "Course not found" -> every { mockCourseRepository.findByName(courseName) } returns null
            "Course found" -> every { mockCourseRepository.findByName(courseName) } returns mockCourse
        }
        // Act and Assert
        when (useCase) {
            "Course not found" -> assertThrows(
                CourseNotFoundException::class.java
            ) { courseService.findByName(mockCourseDto) }

            "Course found" -> {
                val course = courseService.findByName(mockCourseDto)
                verify(exactly = 1) { mockCourseRepository.findByName(courseName) }
                assertNotNull(course)
            }
        }
    }

    @Suppress("unused")
    private fun courseArguments(): Stream<Arguments> =
        Stream.of(
            Arguments.of("Course not found"),
            Arguments.of("Course found")
        )

    @ParameterizedTest
    @MethodSource("coursesArguments")
    fun findAllCourses(
        useCase: String
    ) {
        // Arrange
        when (useCase) {
            "Courses not found" -> every { mockCourseRepository.findAll() } returns mutableSetOf()
            "Courses found" -> every { mockCourseRepository.findAll() } returns mutableSetOf(mockk(relaxed = true))
        }
        // Act
        val courses = courseService.findAllCourses()
        // Assert
        verify(exactly = 1) { mockCourseRepository.findAll() }
        when (useCase) {
            "Courses not found" -> assertTrue(courses.toMutableSet().isEmpty())
            "Courses found" -> Assertions.assertFalse(courses.toMutableSet().isEmpty())
        }
    }

    @Suppress("unused")
    private fun coursesArguments(): Stream<Arguments> =
        Stream.of(
            Arguments.of("Courses not found"),
            Arguments.of("Courses found")
        )

    @ParameterizedTest
    @MethodSource("coursesArguments")
    fun findByStudentEmail(
        useCase: String
    ) {
        // Arrange
        val email = "email"
        when (useCase) {
            "Courses not found" -> every { mockCourseRepository.findByStudentEmail(email) } returns mutableSetOf()
            "Courses found" -> every { mockCourseRepository.findByStudentEmail(email) } returns mutableSetOf(
                mockk(relaxed = true)
            )
        }
        // Act

        val courses = courseService.findByStudentEmail(email)
        // Assert
        verify(exactly = 1) { mockCourseRepository.findByStudentEmail(email) }
        when (useCase) {
            "Courses not found" -> assertTrue(courses.toMutableSet().isEmpty())
            "Courses found" -> Assertions.assertFalse(courses.toMutableSet().isEmpty())
        }
    }

    @ParameterizedTest
    @MethodSource("coursesArguments")
    fun testUpdate(useCase: String) {
        // Arrange
        val courseDto = mockk<CourseDto>(relaxed = true)
        val course = mockk<Course>(relaxed = true)
        when (useCase) {
            "Course not found" -> every { mockCourseRepository.findById(any()) } returns Optional.empty()
            "Course found" -> {
                every { mockCourseRepository.findById(any()) } returns Optional.of(course)
                every { mockCourseRepository.save(any()) } returns course
            }
        }
        // Act and Assert
        when (useCase) {
            "Course not found" -> assertThrows(
                CourseNotFoundException::class.java
            ) { courseService.update(courseDto) }

            "Course found" -> {
                val students = courseService.update(courseDto)
                verify(exactly = 1) { mockCourseRepository.save(any()) }
                assertNotNull(students)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("relateArguments")
    fun relateToStudent(
        useCase: String
    ) {
        // Arrange
        val courseDto = mockk<CourseDto>(relaxed = true)
        val course = mockk<Course>(relaxed = true)
        val studentDto = mockk<StudentDto>(relaxed = true)
        val studentIds = mockk<Set<Long>>(relaxed = true)
        every { mockCourseRepository.findByName(any()) } returns null
        every { mockCourseRepository.save(any()) } returns course
        when (useCase) {
            "Course is related to the Student" -> {
                every { mockCourseRepository.existsById(any()) } returns true
                every { mockStudentRepository.existsById(any()) } returns true
                every { mockStudentRepository.saveAll(setOf()) } returns setOf()
            }

            "StudentNotFoundException - The Student does not exits" -> {
                every { mockStudentRepository.existsById(any()) } returns false
            }

            "EntityAlreadyRelatedException - The Course is already related to the student" -> {
                every { mockCourseRepository.existsById(any()) } returns true
                every { mockStudentRepository.existsById(any()) } returns true
                val mutableSetStudentDto = mutableSetOf<StudentDto>()
                mutableSetStudentDto.add(studentDto)
                every { courseDto.students } returns mutableSetStudentDto
                every { courseDto.toCourse() } returns course
                every { course.toCourseDto() } returns courseDto

                val mutableSetStudent = mutableSetOf<Student>()
                val student = mockk<Student>(relaxed = true)
                mutableSetStudent.add(student)
                every { student.email } returns "test.test@test.de"
                every { mockStudentRepository.findById(any()).get() } returns student

                every { course.students } returns mutableSetStudent
            }

            "CourseNotFoundException - Course does not exists" -> {
                every { mockStudentRepository.existsById(any()) } returns true
                every { mockCourseRepository.existsById(any()) } returns false
            }
        }

        when (useCase) {
            "Course is related to the Student" -> {
                // Act
                courseService.checkAndRelateToStudent(courseDto, studentIds)
                // Arrange
                verify(exactly = 1) { mockStudentRepository.saveAll(setOf()) }
            }

            "StudentNotFoundException - The Student does not exits" -> {
                assertThrows(
                    StudentNotFoundException::class.java
                ) { courseService.checkAndRelateToStudent(courseDto, setOf(1L)) }
            }

            "EntityAlreadyRelatedException - The Course is already related to the student" -> {
                assertThrows(
                    EntityAlreadyRelatedException::class.java
                ) { courseService.checkAndRelateToStudent(courseDto, setOf(1L)) }
            }

            "CourseNotFoundException - Course does not exists" -> {
                assertThrows(
                    CourseNotFoundException::class.java
                ) { courseService.checkAndRelateToStudent(courseDto, studentIds) }
            }
        }
    }

    @Test
    fun testDelete() {
        // Arrange
        every { mockCourseRepository.findByName(any()) } returns mockk(relaxed = true)
        every { mockCourseRepository.delete(any()) } returns Unit
        // Act
        courseService.delete(mockk(relaxed = true))
        // Assert
        verify(exactly = 1) { mockCourseRepository.delete(any()) }
    }

    @ParameterizedTest
    @MethodSource("courseArguments")
    fun testDeleteByName(
        useCase: String
    ) {
        // Arrange
        val courseName = "courseName"
        when (useCase) {
            "Course not found" -> {
                every { mockCourseRepository.findByName(courseName) } returns null
            }

            "Course found" -> {
                // mocking 2 methods at once
                every { mockCourseRepository.findByName(any()) } returns mockk(relaxed = true)
                every { mockStudentRepository.findByCourseName(any()) } returns mockk(relaxed = true)
            }
        }
        every { mockCourseRepository.delete(any()) } returns Unit
        when (useCase) {
            "Course not found" -> {
                assertThrows(
                    CourseNotFoundException::class.java
                ) { courseService.deleteByName(courseName) }
            }

            "Course found" -> {
                courseService.deleteByName(courseName)
                verify(exactly = 1) { mockCourseRepository.delete(any()) }
            }
        }
    }
}
