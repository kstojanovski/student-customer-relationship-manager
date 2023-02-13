package org.acme.scrm.persistence.repository

import org.acme.scrm.persistence.model.Course
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CourseRepository : CrudRepository<Course, Long> {

    @Query("select c from Course c where c.name = ?1")
    fun findByName(courseName: String): Course?

    @Query(
        """
        select c.* from course c
            inner join student_course sc on c.id = sc.course_id
            inner join student s on s.id = sc.student_id
            where s.email = :email
        """,
        nativeQuery = true
    )
    fun findByStudentEmail(@Param("email") email: String): MutableIterable<Course>?
}
