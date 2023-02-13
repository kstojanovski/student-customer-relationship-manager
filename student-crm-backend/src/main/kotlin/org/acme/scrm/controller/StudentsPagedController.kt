package org.acme.scrm.controller

import org.acme.scrm.service.StudentsPagedService
import org.acme.scrm.service.dto.StudentDto
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.PositiveOrZero
import javax.validation.constraints.Size

@CrossOrigin
@RestController
@RequestMapping("/api/v1/students/paged")
@Validated
class StudentsPagedController(val studentsPagedService: StudentsPagedService) {

    @GetMapping("findByFirstname")
    fun findByFirstname(
        @Size(min = 1, max = 20)
        @NotBlank(message = "First name is mandatory")
        @RequestParam
        firstname: String,
        @NotNull
        @RequestParam
        @PositiveOrZero
        page: Int,
        @NotNull
        @RequestParam
        @Positive
        size: Int
    ): Set<StudentDto> {
        return studentsPagedService.findByFirstnameContaining(firstname, page, size)
    }

    @GetMapping("findByLastname")
    fun findByLastname(
        @Size(min = 1, max = 20)
        @NotBlank(message = "Last name is mandatory")
        @RequestParam
        lastname: String,
        @NotNull
        @RequestParam
        @PositiveOrZero
        page: Int,
        @NotNull
        @RequestParam
        @Positive
        size: Int
    ): Set<StudentDto> {
        return studentsPagedService.findByLastnameContaining(lastname, page, size)
    }

    @GetMapping("findByName")
    fun findByName(
        @Size(min = 1, max = 20)
        @NotBlank(message = "First name is mandatory")
        @RequestParam
        firstname: String,
        @Size(min = 1, max = 20)
        @NotBlank(message = "Last name is mandatory")
        @RequestParam
        lastname: String,
        @NotNull
        @RequestParam
        @PositiveOrZero
        page: Int,
        @NotNull
        @RequestParam
        @Positive
        size: Int
    ): Set<StudentDto> {
        return studentsPagedService.findByFirstnameContainingOrLastnameContaining(firstname, lastname, page, size)
    }

    @GetMapping("findByCourseName")
    fun findByCourseName(
        @Size(min = 1, max = 35)
        @NotBlank(message = "Course name is mandatory")
        @RequestParam
        courseName: String,
        @NotNull
        @RequestParam
        @PositiveOrZero
        page: Int,
        @NotNull
        @RequestParam
        @Positive
        size: Int
    ): Set<StudentDto> {
        return studentsPagedService.findByCourseName(courseName, page, size)
    }

    @GetMapping("findByCourseNameNative")
    fun findByCourseNameNative(
        @Size(min = 1, max = 35)
        @NotBlank(message = "Course name is mandatory")
        @RequestParam
        courseName: String,
        @NotNull
        @RequestParam
        @PositiveOrZero
        page: Int,
        @NotNull
        @RequestParam
        @Positive
        size: Int
    ): Set<StudentDto> {
        return studentsPagedService.findByCourseNameNative(courseName, page, size)
    }
}
