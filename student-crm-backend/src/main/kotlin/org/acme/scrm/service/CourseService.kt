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
import org.acme.scrm.service.dto.NewCourseDto
import org.springframework.stereotype.Service

@Service
class CourseService(val courseRepository: CourseRepository, val studentRepository: StudentRepository) {

    // *** CREATE

    fun saveCourse(newCourseDto: NewCourseDto): CourseDto {
        return save(newCourseDto)
    }

    fun saveCourseAndRelate(newCourseDto: NewCourseDto, studentIds: Set<Long>): CourseDto {
        return checkAndRelateToStudent(save(newCourseDto), studentIds)
    }

    private fun save(newCourseDto: NewCourseDto): CourseDto {
        checkCourseDtoAlreadyExists(newCourseDto)
        return courseRepository.save(newCourseDto.toCourse()).toCourseDto()
    }

    private fun checkCourseDtoAlreadyExists(newCourseDto: NewCourseDto) {
        val name = newCourseDto.name
        val course = courseRepository.findByName(name)
        if (course != null) {
            throw CourseAlreadyExistsException("On create course it was found with the name $name")
        }
    }

    // *** READ

    fun findCourse(course: CourseDto): CourseDto {
        return findById(course.id)
    }

    fun findById(id: Long): CourseDto {
        val optionalCourse = courseRepository.findById(id)
        return if (optionalCourse.isPresent) optionalCourse.get().toCourseDto()
        else throw CourseNotFoundException("Course not found!")
    }

    fun findByName(courseDto: CourseDto): CourseDto {
        return findByName(courseDto.name)
    }

    fun findByName(courseName: String): CourseDto {
        return courseRepository.findByName(courseName)?.toCourseDto()
            ?: throw CourseNotFoundException("Course not found!")
    }

    fun findAllCourses(): Iterable<CourseDto> {
        return courseRepository.findAll().map { course -> course.toCourseDto() }.toSet()
    }

    fun findByStudentEmail(email: String): Iterable<CourseDto> {
        return courseRepository.findByStudentEmail(email)?.map { course -> course.toCourseDto() }?.toSet()
            ?: mutableSetOf()
    }

    // *** UPDATE

    fun update(courseDto: CourseDto): CourseDto {
        if (courseRepository.findById(courseDto.id).isEmpty) {
            throw CourseNotFoundException("CourseDto not found.")
        }
        val tempCourse = courseRepository.findByName(courseDto.name)
        if (tempCourse != null && courseDto.id != tempCourse.id) {
            throw CourseAlreadyExistsException("On updating - the course name already exists.")
        }
        return courseRepository.save(courseDto.toCourse()).toCourseDto()
    }

    fun checkAndRelateToStudent(courseDto: CourseDto, studentIds: Set<Long>): CourseDto {
        val students = mutableSetOf<Student>()
        for (studentId in studentIds) {
            if (!studentRepository.existsById(studentId)) {
                throw StudentNotFoundException("On updating the course the student is not found!")
            } else {
                students.add(studentRepository.findById(studentId).get())
            }
        }
        if (courseRepository.existsById(courseDto.id)) {
            return relateToStudent(courseDto.toCourse(), students).toCourseDto()
        } else {
            throw CourseNotFoundException("CourseDto not found for updating!")
        }
    }

    /**
     * does not have any impact on the db since this side is not the owning one,
     * therefore the previous line is added.
     * I would not recommend this many to many JPA approach!
     */
    private fun relateToStudent(
        course: Course,
        students: Set<Student>
    ): Course {
        for (student in students) {
            if (course.students.contains(student)) {
                throw EntityAlreadyRelatedException("CourseDto is already related to the student!")
            }
            student.courses.add(course)
        }
        course.students.addAll(students)
        studentRepository.saveAll(students)
        return course
    }

    // *** DELETE

    fun delete(courseDto: CourseDto) {
        findByName(courseDto)
        courseRepository.delete(courseDto.toCourse())
    }

    fun deleteByName(courseName: String) {
        val courseToBeDeleted = findByName(courseName)
        val students = studentRepository.findByCourseName(courseName).toMutableSet()
        if (students.isNotEmpty()) {
            for (student in students) {
                student.courses.remove(courseToBeDeleted.toCourse())
            }
            studentRepository.saveAll(students)
        }
        delete(courseToBeDeleted)
    }
}
