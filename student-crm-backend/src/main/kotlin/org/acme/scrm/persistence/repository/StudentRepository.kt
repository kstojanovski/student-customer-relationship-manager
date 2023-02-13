package org.acme.scrm.persistence.repository

import org.acme.scrm.persistence.model.Student
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface StudentRepository : CrudRepository<Student, Long> {

    @Query("select s from Student s where s.firstname = ?1 and s.lastname = ?2")
    fun findByName(firstname: String, lastname: String): List<Student>?

    @Query("select s from Student s where s.email = ?1")
    fun findByEmail(email: String): Student?

    @Query(
        """
        select s.* from student s
                inner join student_course sc on s.id = sc.student_id
                inner join course c on c.id = sc.course_id
                where c.name = :courseName
    """,
        nativeQuery = true
    )
    fun findByCourseName(@Param("courseName") courseName: String): MutableIterable<Student>
}
