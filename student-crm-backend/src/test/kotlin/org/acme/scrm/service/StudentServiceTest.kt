package org.acme.scrm.service

import org.acme.scrm.exception.CourseNotFoundException
import org.acme.scrm.exception.EntityAlreadyRelatedException
import org.acme.scrm.exception.StudentAlreadyExistsException
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
import org.junit.jupiter.api.Assertions.assertFalse
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
internal class StudentServiceTest {

    private lateinit var mockStudentRepository: StudentRepository
    private lateinit var mockCourseRepository: CourseRepository
    private lateinit var studentService: StudentService

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
        studentService = StudentService(mockStudentRepository, mockCourseRepository)
    }

    @Test
    fun testSaveStudent() {
        // Arrange
        val studentDto = mockk<StudentDto>(relaxed = true)
        val student = mockk<Student>(relaxed = true)
        every { mockStudentRepository.findByEmail(any()) } returns null
        every { mockStudentRepository.save(any()) } returns student
        // Act
        studentService.saveStudent(studentDto)
        // Arrange
        verify(exactly = 1) { mockStudentRepository.save(any()) }
    }

    @Test
    fun testSaveStudentCheckIfStudentAlreadyExists() {
        // Arrange
        every { mockStudentRepository.findByEmail(any()) } returns Student()
        // Act and Assert
        assertThrows(
            StudentAlreadyExistsException::class.java
        ) { studentService.saveStudent(mockk(relaxed = true)) }
    }

    @ParameterizedTest
    @MethodSource("saveStudentAndRelateArguments")
    fun testSaveStudentAndRelate(
        useCase: String
    ) {
        // Arrange
        every { mockStudentRepository.findByEmail(any()) } returns null
        every { mockStudentRepository.save(any()) } returns mockk(relaxed = true)
        when (useCase) {
            "Student is related to the Course" -> {
                every { mockCourseRepository.existsById(any()) } returns true
                every { mockStudentRepository.existsById(any()) } returns true
            }
        }

        when (useCase) {
            "Student is related to the Course" -> {
                // Act
                studentService.saveStudentAndRelate(mockk(relaxed = true), mockk(relaxed = true))
                // Arrange
                verify(exactly = 2) { mockStudentRepository.save(any()) }
            }
        }
    }

    @Suppress("unused")
    private fun saveStudentAndRelateArguments(): Stream<Arguments> =
        Stream.of(
            Arguments.of("Student is related to the Course"),
            Arguments.of("CourseNotFoundException - Course does not exists"),
            Arguments.of("EntityAlreadyRelatedException - The Course is already related to the student"),
            Arguments.of("StudentNotFoundException - The Student does not exits")
        )

    @ParameterizedTest
    @MethodSource("findByIdArguments")
    fun testFindById(
        useCase: String,
        methodInvokedTimes: Int
    ) {
        // Arrange
        val studentDto = mockk<StudentDto>(relaxed = true)
        when (useCase) {
            "Student found" -> {
                every { mockStudentRepository.findById(any()) } returns Optional.of(mockk(relaxed = true))
            }

            "Student not found" -> every { mockStudentRepository.findById(any()) } returns Optional.empty()
        }

        when (useCase) {
            "Student not found" -> assertThrows(
                StudentNotFoundException::class.java
            ) { studentService.findStudent(studentDto) }

            "Student found" -> {
                val students = studentService.findStudent(studentDto)
                verify(exactly = methodInvokedTimes) { mockStudentRepository.findById(any()) }
                assertNotNull(students)
            }
        }
    }

    @Suppress("unused")
    private fun findByIdArguments(): Stream<Arguments> =
        Stream.of(
            Arguments.of("Student found", 1),
            Arguments.of("Student not found", 1)
        )

    @ParameterizedTest
    @MethodSource("findStudentsIdArguments")
    fun testFindStudents(
        useCase: String
    ) {
        // Arrange
        when (useCase) {
            "Students not found" -> every { mockStudentRepository.findAll() } returns mutableSetOf()
            "Students found" -> every { mockStudentRepository.findAll() } returns mutableSetOf(mockk(relaxed = true))
        }
        // Act
        val students = studentService.findAllStudents()
        // Assert
        verify(exactly = 1) { mockStudentRepository.findAll() }
        when (useCase) {
            "Students not found" -> assertTrue(students.toMutableSet().isEmpty())
            "Students found" -> assertFalse(students.toMutableSet().isEmpty())
        }
    }

    @Suppress("unused")
    private fun findStudentsIdArguments(): Stream<Arguments> =
        Stream.of(
            Arguments.of("Students not found"),
            Arguments.of("Students found")
        )

    @ParameterizedTest
    @MethodSource("findStudentsIdArguments")
    fun testFindByCourseName(
        useCase: String
    ) {
        // Arrange
        val courseName = "someCourseName"
        when (useCase) {
            "Students not found" -> every { mockStudentRepository.findByCourseName(courseName) } returns mutableSetOf()
            "Students found" -> every { mockStudentRepository.findByCourseName(courseName) } returns mutableSetOf(
                mockk(relaxed = true)
            )
        }
        // Act

        val students = studentService.findByCourseName(courseName)
        // Assert
        verify(exactly = 1) { mockStudentRepository.findByCourseName(courseName) }
        when (useCase) {
            "Students not found" -> assertTrue(students.toMutableSet().isEmpty())
            "Students found" -> assertFalse(students.toMutableSet().isEmpty())
        }
    }

    @ParameterizedTest
    @MethodSource("studentArguments")
    fun testFindByEmail(
        useCase: String
    ) {
        // Arrange
        val student = mockk<Student>(relaxed = true)
        val email = "someEmail"
        when (useCase) {
            "Student not found" -> every { mockStudentRepository.findByEmail(email) } returns null
            "Student found" -> every { mockStudentRepository.findByEmail(email) } returns student
        }
        // Act and Assert
        when (useCase) {
            "Student not found" -> assertThrows(
                StudentNotFoundException::class.java
            ) { studentService.findByEmail(email) }

            "Student found" -> {
                val students = studentService.findByEmail(email)
                verify(exactly = 1) { mockStudentRepository.findByEmail(email) }
                assertNotNull(students)
            }
        }
    }

    @Suppress("unused")
    private fun studentArguments(): Stream<Arguments> =
        Stream.of(
            Arguments.of("Student not found"),
            Arguments.of("Student found")
        )

    @ParameterizedTest
    @MethodSource("studentArguments")
    fun testUpdate(useCase: String) {
        // Arrange
        val studentDto = mockk<StudentDto>(relaxed = true)
        val student = mockk<Student>(relaxed = true)
        val tempStudent = mockk<Student>(relaxed = true)
        when (useCase) {
            "Student not found" -> every { mockStudentRepository.findById(any()) } returns Optional.empty()
            "Student found" -> {
                every { mockStudentRepository.findByEmail(any()) } returns tempStudent
                every { mockStudentRepository.findById(any()) } returns Optional.of(student)
                every { mockStudentRepository.save(any()) } returns student
            }
        }
        // Act and Assert
        when (useCase) {
            "Student not found" -> assertThrows(
                StudentNotFoundException::class.java
            ) { studentService.update(studentDto) }

            "Student found" -> {
                val students = studentService.update(studentDto)
                verify(exactly = 1) { mockStudentRepository.save(any()) }
                assertNotNull(students)
            }
        }
    }

    @ParameterizedTest
    @MethodSource("saveStudentAndRelateArguments")
    fun testRelateToCourse(
        useCase: String
    ) {
        // Arrange
        val studentDto = mockk<StudentDto>(relaxed = true)
        val student = mockk<Student>(relaxed = true)
        val courseDto = mockk<CourseDto>(relaxed = true)
        val courseIds = mockk<Set<Long>>(relaxed = true)

        every { mockStudentRepository.findByEmail(any()) } returns null
        every { mockStudentRepository.save(any()) } returns student
        when (useCase) {
            "Student is related to the Course" -> {
                every { mockCourseRepository.existsById(any()) } returns true
                every { mockStudentRepository.existsById(any()) } returns true
            }

            "CourseNotFoundException - Course does not exists" -> {
                every { mockCourseRepository.existsById(any()) } returns false
            }

            "EntityAlreadyRelatedException - The Course is already related to the student" -> {
                every { mockCourseRepository.existsById(any()) } returns true
                every { mockStudentRepository.existsById(any()) } returns true
                val mutableSetCourseDto = mutableSetOf<CourseDto>()
                every { courseDto.name } returns "CName"
                mutableSetCourseDto.add(courseDto)
                every { studentDto.courses } returns mutableSetCourseDto
                every { studentDto.toStudent() } returns student
                every { student.toStudentDto() } returns studentDto

                val mutableSetCourse = mutableSetOf<Course>()
                val course = mockk<Course>(relaxed = true)
                mutableSetCourse.add(course)
                every { course.name } returns "CName"
                every { mockCourseRepository.findById(any()).get() } returns course

                every { student.courses } returns mutableSetCourse
            }

            "StudentNotFoundException - The Student does not exits" -> {
                every { mockCourseRepository.existsById(any()) } returns true
                every { mockStudentRepository.existsById(any()) } returns false
            }
        }

        when (useCase) {
            "Student is related to the Course" -> {
                // Act
                studentService.checkAndRelateToCourses(studentDto, courseIds)
                // Arrange
                verify(exactly = 1) { mockStudentRepository.save(any()) }
            }

            "CourseNotFoundException - Course does not exists" -> {
                assertThrows(
                    CourseNotFoundException::class.java
                ) { studentService.checkAndRelateToCourses(studentDto, setOf(1L)) }
            }

            "EntityAlreadyRelatedException - The Course is already related to the student" -> {
                assertThrows(
                    EntityAlreadyRelatedException::class.java
                ) { studentService.checkAndRelateToCourses(studentDto, setOf(1L)) }
            }

            "StudentNotFoundException - The Student does not exits" -> {
                assertThrows(
                    StudentNotFoundException::class.java
                ) { studentService.checkAndRelateToCourses(studentDto, courseIds) }
            }
        }
    }

    @Test
    fun testDelete() {
        // Arrange
        every { mockStudentRepository.delete(any()) } returns Unit
        every { mockStudentRepository.findByEmail(any()) } returns mockk(relaxed = true)
        // Act
        studentService.delete(mockk(relaxed = true))
        // Assert
        verify(exactly = 1) { mockStudentRepository.delete(any()) }
    }

    @ParameterizedTest
    @MethodSource("studentArguments")
    fun testDeleteByEmail(
        useCase: String
    ) {
        // Arrange
        val email = "email"
        when (useCase) {
            "Student not found" -> every { mockStudentRepository.findByEmail(email) } returns null
            "Student found" -> every { mockStudentRepository.findByEmail(email) } returns mockk(relaxed = true)
        }
        every { mockStudentRepository.delete(any()) } returns Unit

        when (useCase) {
            "Student not found" -> {
                assertThrows(
                    StudentNotFoundException::class.java
                ) { studentService.deleteByEmail(email) }
            }

            "Student found" -> {
                // Act
                studentService.deleteByEmail(email)
                // Assert
                verify(exactly = 1) { mockStudentRepository.delete(any()) }
            }
        }
    }
}
