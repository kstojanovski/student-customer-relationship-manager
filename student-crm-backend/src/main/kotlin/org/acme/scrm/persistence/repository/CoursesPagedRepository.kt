package org.acme.scrm.persistence.repository

import org.acme.scrm.persistence.model.Course
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CoursesPagedRepository : JpaRepository<Course, Long> {

    @Query(
        value =
        """
        select c.* from course c
            inner join student_course sc on c.id = sc.course_id
            inner join student s on s.id = sc.student_id
            where s.email = :email
            order by c.name            
        """,
        countQuery =
        """
        select count(c.*) from course c
            inner join student_course sc on c.id = sc.course_id
            inner join student s on s.id = sc.student_id
            where s.email = :email
        """,
        nativeQuery = true
    )
    fun findByStudentEmail(@Param("email") email: String, pageable: Pageable): Page<Course>

    @Query(
        value =
        """
        select c.* from course c
            inner join student_course sc on c.id = sc.course_id
            inner join student s on s.id = sc.student_id
            where s.email = :email
            order by c.name
            limit :limit offset :offset
        """,
        nativeQuery = true
    )
    fun findByStudentEmailNative(
        @Param("email") email: String,
        @Param("limit") limit: Int,
        @Param("offset") offset: Int
    ): Set<Course>
}
