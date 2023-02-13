package org.acme.scrm.persistence.repository

import org.acme.scrm.persistence.model.Student
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

private const val LASTNAME = "lastname"

private const val FIRSTNAME = "firstname"

/**
 * The name "StudentsPagedRepositoryImpl" is used by the spring itself for creating an instance of the
 * StudentPagedRepository interface, therefore it is not used here.
 */
@Repository
class StudentsPagedRepositoryImp(var studentsPagedRepository: StudentsPagedRepository) {

    fun findByFirstnameContaining(firstname: String, page: Int, size: Int): Iterable<Student> {
        return studentsPagedRepository.findByFirstnameContaining(
            firstname,
            pageRequestFirstLast(page, size)
        )
    }

    fun findByLastnameContaining(lastname: String, page: Int, size: Int): Iterable<Student> {
        return studentsPagedRepository.findByLastnameContaining(
            lastname,
            pageRequestAndSort(
                page,
                size,
                listOf(
                    Sort.Order(Sort.Direction.ASC, LASTNAME),
                    Sort.Order(Sort.Direction.ASC, FIRSTNAME)
                )
            )
        )
    }

    fun findByFirstnameContainingOrLastnameContaining(
        firstname: String,
        lastname: String,
        page: Int,
        size: Int
    ): Iterable<Student> {
        return studentsPagedRepository.findByFirstnameContainingOrLastnameContaining(
            firstname,
            lastname,
            pageRequestFirstLast(page, size)
        )
    }

    fun findByCourseName(
        courseName: String,
        page: Int,
        size: Int
    ): Iterable<Student> {
        return studentsPagedRepository.findByCourseName(
            courseName,
            pageRequestFirstLast(page, size)
        )
    }

    fun findByCourseNameNative(
        courseName: String,
        page: Int,
        size: Int
    ): Iterable<Student> {
        return studentsPagedRepository.findByCourseName(courseName, size, page * size)
    }

    internal fun pageRequestFirstLast(page: Int, size: Int) = pageRequestAndSort(
        page,
        size,
        listOf(
            Sort.Order(Sort.Direction.ASC, FIRSTNAME),
            Sort.Order(Sort.Direction.ASC, LASTNAME)
        )
    )

    private fun pageRequestAndSort(page: Int, size: Int, orders: List<Sort.Order>): PageRequest {
        return PageRequest.of(page, size, Sort.by(orders))
    }
}
