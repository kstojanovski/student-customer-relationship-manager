package org.acme.scrm.persistence.repository

import org.acme.scrm.persistence.model.Course
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

/**
 * The name "CoursesPagedRepositoryImpl" is used by the spring itself for creating an instance of the
 * CoursesPagedRepository interface, therefore it is not used here.
 */
@Repository
class CoursesPagedRepositoryImp(val coursesPagedRepository: CoursesPagedRepository) {

    fun findByStudentEmail(
        email: String,
        page: Int,
        size: Int
    ): Iterable<Course> {
        return coursesPagedRepository.findByStudentEmail(
            email,
            PageRequest.of(page, size, Sort.by("s.lastname", "s.firstname").ascending())
        )
    }

    fun findByStudentEmailNative(
        email: String,
        limit: Int,
        offset: Int
    ): Set<Course> {
        return coursesPagedRepository.findByStudentEmailNative(email, limit, offset)
    }
}
