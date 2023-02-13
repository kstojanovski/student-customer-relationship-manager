package org.acme.scrm.persistence.model

import org.acme.scrm.service.dto.CourseDto
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "course")
open class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_generator")
    @SequenceGenerator(name = "course_generator", sequenceName = "course_id_seq", allocationSize = 1)
    @Column(name = "id")
    open var id: Long? = null

    @NotBlank(message = "Course name is mandatory")
    @Size(min = 1, max = 35)
    @Column(name = "name", length = 35, nullable = false, unique = true)
    open lateinit var name: String

    @ManyToMany(mappedBy = "courses") // lazy by default
    open var students: MutableSet<Student> = HashSet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Course

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    fun toCourseDto(): CourseDto {
        return CourseDto(
            id = id!!,
            name = name,
            students = students.map { student -> student.toLightStudentDto() }.toMutableSet()
        )
    }

    fun toLightCourseDto(): CourseDto {
        return CourseDto(
            id = id!!,
            name = name,
            students = mutableSetOf()
        )
    }
}
