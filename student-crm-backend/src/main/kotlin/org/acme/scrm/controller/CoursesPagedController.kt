package org.acme.scrm.controller

import org.acme.scrm.service.CoursesPagedService
import org.acme.scrm.service.dto.CourseDto
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.PositiveOrZero

@CrossOrigin
@RestController
@RequestMapping("/api/v1/courses/paged")
@Validated
class CoursesPagedController(val coursesPagedService: CoursesPagedService) {

    @GetMapping("findByStudentEmail")
    fun findByStudentEmail(
        @Email
        @NotBlank(message = "Email is mandatory")
        @RequestParam
        email: String,
        @NotNull
        @RequestParam
        @PositiveOrZero
        page: Int,
        @NotNull
        @RequestParam
        @Positive
        size: Int
    ): Set<CourseDto> {
        return coursesPagedService.findByStudentEmail(email, page, size)
    }

    @GetMapping("findByStudentEmailNative")
    fun findByStudentEmailNative(
        @Email
        @NotBlank(message = "Email is mandatory")
        @RequestParam
        email: String,
        @NotNull
        @RequestParam
        @PositiveOrZero
        page: Int,
        @NotNull
        @RequestParam
        @Positive
        size: Int
    ): Set<CourseDto> {
        return coursesPagedService.findByStudentEmailNative(email, page, size)
    }
}
