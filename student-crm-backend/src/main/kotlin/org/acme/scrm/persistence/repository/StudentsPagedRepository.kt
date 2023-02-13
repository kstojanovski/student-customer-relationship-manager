package org.acme.scrm.persistence.repository

import org.acme.scrm.persistence.model.Student
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StudentsPagedRepository : JpaRepository<Student, Long> {
    fun findByFirstnameContaining(firstname: String, pageable: Pageable): Page<Student>
    fun findByLastnameContaining(lastname: String, pageable: Pageable): Page<Student>
    fun findByFirstnameContainingOrLastnameContaining(firstname: String, lastname: String, pageable: Pageable):
        Page<Student>

    @Query(
        value = """
        select s.* from student s
                inner join student_course sc on s.id = sc.student_id
                inner join course c on c.id = sc.course_id
                where c.name = :courseName
                order by s.firstname, s.lastname""",
        countQuery = """
        select count(s.*) from student s
                inner join student_course sc on s.id = sc.student_id
                inner join course c on c.id = sc.course_id
                where c.name = :courseName""",
        nativeQuery = true
    )
    fun findByCourseName(@Param("courseName") courseName: String, pageable: Pageable): Page<Student>

    @Query(
        value = """
        select s.* from student s
                inner join student_course sc on s.id = sc.student_id
                inner join course c on c.id = sc.course_id
                where c.name = :courseName
                order by s.firstname, s.lastname
                limit :limit offset :offset""",
        nativeQuery = true
    )
    fun findByCourseName(
        @Param("courseName") courseName: String,
        @Param("limit") limit: Int,
        @Param("offset") offset: Int
    ): Set<Student>
}
