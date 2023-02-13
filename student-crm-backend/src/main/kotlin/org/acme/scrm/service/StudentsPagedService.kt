package org.acme.scrm.service

import org.acme.scrm.persistence.model.Student
import org.acme.scrm.persistence.repository.StudentsPagedRepositoryImp
import org.acme.scrm.service.dto.StudentDto
import org.springframework.stereotype.Service

@Service
class StudentsPagedService(private val studentsPagedRepositoryImp: StudentsPagedRepositoryImp) {

    fun findByFirstnameContaining(firstname: String, page: Int, size: Int): Set<StudentDto> {
        return convertToStudentDtoSet(studentsPagedRepositoryImp.findByFirstnameContaining(firstname, page, size))
    }

    fun findByLastnameContaining(lastname: String, page: Int, size: Int): Set<StudentDto> {
        return convertToStudentDtoSet(studentsPagedRepositoryImp.findByLastnameContaining(lastname, page, size))
    }

    fun findByFirstnameContainingOrLastnameContaining(
        firstname: String,
        lastname: String,
        page: Int,
        size: Int
    ): Set<StudentDto> {
        return convertToStudentDtoSet(
            studentsPagedRepositoryImp.findByFirstnameContainingOrLastnameContaining(firstname, lastname, page, size)
        )
    }

    fun findByCourseName(
        courseName: String,
        page: Int,
        size: Int
    ): Set<StudentDto> {
        return convertToStudentDtoSet(studentsPagedRepositoryImp.findByCourseName(courseName, page, size))
    }

    fun findByCourseNameNative(
        courseName: String,
        page: Int,
        size: Int
    ): Set<StudentDto> {
        return convertToStudentDtoSet(studentsPagedRepositoryImp.findByCourseNameNative(courseName, page, size))
    }

    internal fun convertToStudentDtoSet(courseIterable: Iterable<Student>): Set<StudentDto> =
        courseIterable.map { student -> student.toStudentDto() }.toSet()
}
