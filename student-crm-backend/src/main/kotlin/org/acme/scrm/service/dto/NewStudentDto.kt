package org.acme.scrm.service.dto

import org.acme.scrm.persistence.model.Student
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

open class NewStudentDto(
    @Size(max = 20)
    @NotBlank(message = "First name is mandatory")
    open var firstname: String,
    @Size(max = 20)
    @NotBlank(message = "Last name is mandatory")
    open var lastname: String,
    @Email
    @NotBlank(message = "Email is mandatory")
    open var email: String
) {
    open fun toStudent(): Student {
        val student = Student()
        student.id = null
        student.firstname = firstname
        student.lastname = lastname
        student.email = email
        student.courses = mutableSetOf()
        return student
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NewStudentDto

        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        return email.hashCode()
    }
}
