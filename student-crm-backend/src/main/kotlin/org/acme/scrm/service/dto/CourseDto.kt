package org.acme.scrm.service.dto

import org.acme.scrm.persistence.model.Course
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

class CourseDto(
    @NotNull
    @Positive
    val id: Long,
    @NotBlank(message = "Course name is mandatory")
    @Size(max = 35)
    override var name: String,
    @NotNull
    var students: MutableSet<StudentDto>
) : NewCourseDto(name) {
    override fun toCourse(): Course {
        val course = toLightCourse()
        course.students = students.map { studentDto -> studentDto.toLightStudent() }.toMutableSet()
        return course
    }

    fun toLightCourse(): Course {
        val course = super.toCourse()
        course.id = id
        return course
    }
}
