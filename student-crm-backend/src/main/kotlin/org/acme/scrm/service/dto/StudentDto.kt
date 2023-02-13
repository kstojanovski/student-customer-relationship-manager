package org.acme.scrm.service.dto

import org.acme.scrm.persistence.model.Student
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

class StudentDto(
    @NotNull
    @Positive
    var id: Long,
    @Size(max = 20)
    @NotBlank(message = "First name is mandatory")
    override var firstname: String,
    @Size(max = 20)
    @NotBlank(message = "Last name is mandatory")
    override var lastname: String,
    @Email
    @NotBlank(message = "Email is mandatory")
    override var email: String,
    @NotNull
    var courses: MutableSet<CourseDto>
) : NewStudentDto(firstname, lastname, email) {
    override fun toStudent(): Student {
        val student = toLightStudent()
        student.courses = courses.map { courseDto -> courseDto.toLightCourse() }.toMutableSet()
        return student
    }

    fun toLightStudent(): Student {
        val student = super.toStudent()
        student.id = id
        return student
    }
}
