package org.acme.scrm.controller

import org.acme.scrm.service.StudentService
import org.acme.scrm.service.dto.StudentDto
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@CrossOrigin
@RestController
@RequestMapping("/api/v1/students")
@Validated
class StudentsController(val studentService: StudentService) {

    @GetMapping("getAll")
    fun getAll(): Iterable<StudentDto> {
        return studentService.findAllStudents()
    }

    @GetMapping("findByCourseName")
    fun findByCourseName(
        @Size(min = 1, max = 35)
        @NotBlank(message = "Course name is mandatory")
        @RequestParam
        courseName: String
    ): Iterable<StudentDto> {
        return studentService.findByCourseName(courseName)
    }
}
