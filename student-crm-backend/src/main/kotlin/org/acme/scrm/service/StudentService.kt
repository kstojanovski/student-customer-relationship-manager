package org.acme.scrm.service

import org.acme.scrm.exception.CourseNotFoundException
import org.acme.scrm.exception.EntityAlreadyRelatedException
import org.acme.scrm.exception.StudentAlreadyExistsException
import org.acme.scrm.exception.StudentNotFoundException
import org.acme.scrm.persistence.model.Course
import org.acme.scrm.persistence.model.Student
import org.acme.scrm.persistence.repository.CourseRepository
import org.acme.scrm.persistence.repository.StudentRepository
import org.acme.scrm.service.dto.NewStudentDto
import org.acme.scrm.service.dto.StudentDto
import org.springframework.stereotype.Service

@Service
class StudentService(val studentRepository: StudentRepository, val courseRepository: CourseRepository) {

    // *** CREATE

    fun saveStudent(newStudentDto: NewStudentDto): StudentDto {
        return save(newStudentDto)
    }

    fun saveStudentAndRelate(newStudentDto: NewStudentDto, courseIds: Set<Long>): StudentDto {
        return checkAndRelateToCourses(save(newStudentDto), courseIds)
    }

    private fun save(newStudentDto: NewStudentDto): StudentDto {
        checkIfStudentAlreadyExists(newStudentDto)
        return studentRepository.save(newStudentDto.toStudent()).toStudentDto()
    }

    private fun checkIfStudentAlreadyExists(studentDto: NewStudentDto) {
        val email = studentDto.email
        val student = studentRepository.findByEmail(email)
        if (student != null) {
            throw StudentAlreadyExistsException("On create student it was found with the e-mail $email")
        }
    }

    // *** READ

    fun findStudent(studentDto: StudentDto): StudentDto {
        val studentOptional = studentDto.id.let { studentRepository.findById(it) }
        return if (studentOptional.isPresent) studentOptional.get().toStudentDto()
        else throw StudentNotFoundException("StudentDto not found.")
    }

    fun findAllStudents(): Iterable<StudentDto> {
        return studentRepository.findAll().map { student -> student.toStudentDto() }.toSet()
    }

    fun findByCourseName(courseName: String): Iterable<StudentDto> {
        return studentRepository.findByCourseName(courseName).map { student -> student.toStudentDto() }.toSet()
    }

    fun findByEmail(email: String): StudentDto {
        return studentRepository.findByEmail(email)?.toStudentDto()
            ?: throw StudentNotFoundException("StudentDto not found of searching its e-mail address.")
    }

    // *** UPDATE

    fun update(studentDto: StudentDto): StudentDto {
        if (studentRepository.findById(studentDto.id).isEmpty) {
            throw StudentNotFoundException("StudentDto not found.")
        }
        val tempStudent = studentRepository.findByEmail(studentDto.email)
        if (tempStudent != null && studentDto.id != tempStudent.id) {
            throw StudentAlreadyExistsException("On updating - the course name already exists.")
        }
        return studentRepository.save(studentDto.toStudent()).toStudentDto()
    }

    fun checkAndRelateToCourses(studentDto: StudentDto, courseIds: Set<Long>): StudentDto {
        val courses = mutableSetOf<Course>()
        for (courseId in courseIds) {
            if (!courseRepository.existsById(courseId)) {
                throw CourseNotFoundException("On updating the student the course is not found!")
            } else {
                courses.add(courseRepository.findById(courseId).get())
            }
        }
        if (studentRepository.existsById(studentDto.id)) {
            return relateToCourses(studentDto.toStudent(), courses).toStudentDto()
        } else {
            throw StudentNotFoundException("StudentDto not found for updating!")
        }
    }

    private fun relateToCourses(
        student: Student,
        courses: MutableSet<Course>
    ): Student {
        for (course in courses) {
            if (student.courses.contains(course)) {
                throw EntityAlreadyRelatedException("StudentDto is already related to the course!")
            }
        }
        student.courses.addAll(courses)
        return studentRepository.save(student)
    }

    // *** DELETE

    fun delete(studentDto: StudentDto) {
        findByEmail(studentDto.email)
        studentRepository.delete(studentDto.toStudent())
    }

    fun deleteByEmail(email: String) {
        findByEmail(email)
        studentRepository.findByEmail(email)?.let { studentRepository.delete(it) }
    }
}
