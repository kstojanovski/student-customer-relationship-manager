package org.acme.scrm.persistence.model

import org.acme.scrm.service.dto.StudentDto
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Entity
@Table(name = "student")
open class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_generator")
    @SequenceGenerator(name = "student_generator", sequenceName = "student_id_seq", allocationSize = 1)
    @Column(name = "id")
    open var id: Long? = null

    @Size(min = 1, max = 20)
    @NotBlank(message = "First name is mandatory")
    @Column(name = "firstname", length = 20, nullable = false)
    open lateinit var firstname: String

    @Size(min = 1, max = 20)
    @NotBlank(message = "Last name is mandatory")
    @Column(name = "lastname", length = 20, nullable = false)
    open lateinit var lastname: String

    @Email
    @NotBlank(message = "Email is mandatory")
    @Column(name = "email", nullable = false, unique = true)
    open lateinit var email: String

    // lazy by default
    @ManyToMany
    @JoinTable(
        name = "student_course",
        joinColumns = [JoinColumn(name = "student_id")],
        inverseJoinColumns = [JoinColumn(name = "course_id")]
    )
    open
    var courses: MutableSet<Course> = HashSet()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Student

        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }

    fun toStudentDto(): StudentDto {
        return StudentDto(
            id = id!!,
            firstname = firstname,
            lastname = lastname,
            email = email,
            courses = courses.map { course -> course.toLightCourseDto() }.toMutableSet()
        )
    }

    fun toLightStudentDto(): StudentDto {
        return StudentDto(
            id = id!!,
            firstname = firstname,
            lastname = lastname,
            email = email,
            courses = mutableSetOf()
        )
    }
}
