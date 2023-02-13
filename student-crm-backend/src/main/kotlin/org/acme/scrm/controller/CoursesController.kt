package org.acme.scrm.controller

import org.acme.scrm.service.CourseService
import org.acme.scrm.service.dto.CourseDto
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
@RequestMapping("/api/v1/courses")
@Validated
class CoursesController(val courseService: CourseService) {

    @GetMapping("getAll")
    fun findAll(): Iterable<CourseDto> {
        return courseService.findAllCourses()
    }

    @GetMapping("findByStudentEmail")
    fun findByStudentEmail(
        @Size(min = 1, max = 35)
        @NotBlank(message = "Email is mandatory")
        @RequestParam
        email: String
    ): Iterable<CourseDto> {
        return courseService.findByStudentEmail(email)
    }
}
