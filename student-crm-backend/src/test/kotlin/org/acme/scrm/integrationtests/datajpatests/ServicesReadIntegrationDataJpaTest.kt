package org.acme.scrm.integrationtests.datajpatests

import org.acme.scrm.integrationtests.PostgresTestContainer
import org.acme.scrm.persistence.model.Course
import org.acme.scrm.persistence.model.Student
import org.acme.scrm.persistence.repository.CourseRepository
import org.acme.scrm.persistence.repository.CoursesPagedRepository
import org.acme.scrm.persistence.repository.CoursesPagedRepositoryImp
import org.acme.scrm.persistence.repository.StudentRepository
import org.acme.scrm.persistence.repository.StudentsPagedRepository
import org.acme.scrm.persistence.repository.StudentsPagedRepositoryImp
import org.acme.scrm.service.CourseService
import org.acme.scrm.service.CoursesPagedService
import org.acme.scrm.service.StudentService
import org.acme.scrm.service.StudentsPagedService
import org.acme.scrm.service.dto.NewCourseDto
import org.acme.scrm.service.dto.NewStudentDto
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ServicesReadIntegrationDataJpaTest : PostgresTestContainer {
    @Autowired
    private lateinit var studentRepository: StudentRepository

    @Autowired
    private lateinit var studentsPagedRepository: StudentsPagedRepository
    private lateinit var studentsPagedRepositoryImp: StudentsPagedRepositoryImp

    @Autowired
    private lateinit var courseRepository: CourseRepository

    @Autowired
    private lateinit var coursesPagedRepository: CoursesPagedRepository
    private lateinit var coursesPagedRepositoryImp: CoursesPagedRepositoryImp

    private lateinit var studentService: StudentService
    private lateinit var courseService: CourseService

    @BeforeEach
    fun prepare() {
        studentRepository.deleteAll()
        courseRepository.deleteAll()
        coursesPagedRepositoryImp = CoursesPagedRepositoryImp(coursesPagedRepository)
        studentsPagedRepositoryImp = StudentsPagedRepositoryImp(studentsPagedRepository)
        studentService = StudentService(studentRepository, courseRepository)
        courseService = CourseService(courseRepository, studentRepository)
    }

    @Test
    fun testFindFromPagedRepositories() {
        // Arrange
        val students = mutableSetOf(
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

        val studentsPagedService = StudentsPagedService(studentsPagedRepositoryImp)
        val coursesPagedService = CoursesPagedService(coursesPagedRepositoryImp)

        // Act and Assert
        var actualStudents = studentsPagedService.findByFirstnameContaining("John", 0, Int.MAX_VALUE)
        Assertions.assertEquals(4, actualStudents.size)
        actualStudents = studentsPagedService.findByFirstnameContaining("Dirty", 0, Int.MAX_VALUE)
        Assertions.assertEquals(1, actualStudents.size)
        actualStudents =
            studentsPagedService.findByFirstnameContainingOrLastnameContaining("Harry", "Harry", 0, Int.MAX_VALUE)
        Assertions.assertEquals(2, actualStudents.size)

        actualStudents = studentsPagedService.findByCourseName("Pacifism", 0, Int.MAX_VALUE)
        Assertions.assertEquals(2, actualStudents.size)
        actualStudents = studentsPagedService.findByCourseName("Veganism", 0, Int.MAX_VALUE)
        Assertions.assertEquals(3, actualStudents.size)
        actualStudents = studentsPagedService.findByCourseNameNative("Pacifism", 0, Int.MAX_VALUE)
        Assertions.assertEquals(2, actualStudents.size)
        actualStudents = studentsPagedService.findByCourseNameNative("Veganism", 0, Int.MAX_VALUE)
        Assertions.assertEquals(3, actualStudents.size)

        var actualCourses = coursesPagedService.findByStudentEmail("john.rambo@pacifists.com", 0, Int.MAX_VALUE)
        Assertions.assertEquals(4, actualCourses.size)
        actualCourses = coursesPagedService.findByStudentEmail("john.wayne@pacifists.com", 0, Int.MAX_VALUE)
        Assertions.assertEquals(0, actualCourses.size)
        actualCourses = coursesPagedService.findByStudentEmailNative("john.rambo@pacifists.com", 0, Int.MAX_VALUE)
        Assertions.assertEquals(4, actualCourses.size)
        actualCourses = coursesPagedService.findByStudentEmailNative("john.wayne@pacifists.com", 0, Int.MAX_VALUE)
        Assertions.assertEquals(0, actualCourses.size)
    }

    @Test
    fun testPagination() {
        val courseService = CourseService(courseRepository, studentRepository)
        val courseCS = "Computer Science"
        courseService.saveCourse(NewCourseDto(courseCS))
        val course = courseService.findByName(courseCS)

        val studentService = StudentService(studentRepository, courseRepository)
        for (i in 1..40) {
            studentService.saveStudentAndRelate(
                NewStudentDto("John$i", "Doe", "john$i.doe@uni.edu"),
                setOf(course.id)
            )
        }

        val studentsPagedService = StudentsPagedService(studentsPagedRepositoryImp)
        val size = 10
        for (page in 0..1) {
            Assertions.assertEquals(
                studentsPagedService.findByCourseName(courseCS, page, size),
                studentsPagedService.findByCourseNameNative(courseCS, page, size)
            )
        }
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
