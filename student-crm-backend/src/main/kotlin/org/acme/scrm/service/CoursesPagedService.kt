package org.acme.scrm.service

import org.acme.scrm.persistence.model.Course
import org.acme.scrm.persistence.repository.CoursesPagedRepositoryImp
import org.acme.scrm.service.dto.CourseDto
import org.springframework.stereotype.Service

@Service
class CoursesPagedService(val coursesPagedRepositoryImp: CoursesPagedRepositoryImp) {

    fun findByStudentEmail(
        email: String,
        page: Int,
        size: Int
    ): Set<CourseDto> {
        return convertToCourseDtoSet(coursesPagedRepositoryImp.findByStudentEmail(email, page, size))
    }

    fun findByStudentEmailNative(
        email: String,
        page: Int,
        size: Int
    ): Set<CourseDto> {
        return convertToCourseDtoSet(coursesPagedRepositoryImp.findByStudentEmailNative(email, size, page * size))
    }

    private fun convertToCourseDtoSet(courseIterable: Iterable<Course>): Set<CourseDto> =
        courseIterable.map { course -> course.toCourseDto() }.toSet()
}
