package org.acme.scrm.controller

import org.acme.scrm.service.CourseService
import org.acme.scrm.service.dto.CourseDto
import org.acme.scrm.service.dto.NewCourseDto
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@CrossOrigin
@RestController
@RequestMapping("/api/v1/course")
@Validated
class CourseController(val courseService: CourseService) {

    @PostMapping("create")
    fun createCourse(
        @Valid @RequestBody
        newCourseDto: NewCourseDto
    ): CourseDto {
        return courseService.saveCourse(newCourseDto)
    }

    @PostMapping("addAndRelate")
    fun addCourseAndRelate(
        @Valid @RequestBody
        newCourseDto: NewCourseDto,
        @Size(min = 1)
        @RequestParam
        studentIds: Set<Long>
    ): CourseDto {
        return courseService.saveCourseAndRelate(newCourseDto, studentIds)
    }

    @GetMapping("find")
    fun findCourse(
        @Valid @RequestBody
        courseDto: CourseDto
    ): CourseDto {
        return courseService.findCourse(courseDto)
    }

    @GetMapping("findByName")
    fun findByName(
        @NotBlank(message = "Course name is mandatory")
        @Size(min = 1, max = 35)
        @RequestParam
        name: String
    ): CourseDto? {
        return courseService.findByName(name)
    }

    @PutMapping("update")
    fun update(
        @Valid @RequestBody
        courseDto: CourseDto
    ): CourseDto {
        return courseService.update(courseDto)
    }

    @PutMapping("relateToStudent")
    fun relateToStudent(
        @Valid @RequestBody
        courseDto: CourseDto,
        @Size(min = 1)
        @RequestParam
        studentIds: Set<Long>
    ): CourseDto {
        return courseService.checkAndRelateToStudent(courseDto, studentIds)
    }

    @DeleteMapping("delete")
    fun delete(
        @Valid @RequestBody
        courseDto: CourseDto
    ) {
        courseService.delete(courseDto)
    }

    @DeleteMapping("deleteByName")
    fun deleteByName(
        @Valid @RequestBody
        courseDto: CourseDto
    ) {
        courseService.deleteByName(courseDto.name)
    }
}
