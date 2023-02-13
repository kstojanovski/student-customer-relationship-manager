package org.acme.scrm.service.dto

import org.acme.scrm.persistence.model.Course
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

open class NewCourseDto(
    @NotBlank(message = "Course name is mandatory")
    @Size(max = 35)
    open var name: String
) {
    open fun toCourse(): Course {
        val course = Course()
        course.id = null
        course.name = name
        course.students = mutableSetOf()
        return course
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewCourseDto

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
