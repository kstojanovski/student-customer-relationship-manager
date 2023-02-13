package org.acme.scrm.controller

import org.acme.scrm.service.StudentService
import org.acme.scrm.service.dto.NewStudentDto
import org.acme.scrm.service.dto.StudentDto
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
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@CrossOrigin
@RestController
@RequestMapping("/api/v1/student")
@Validated
class StudentController(val studentService: StudentService) {

    @PostMapping("create")
    fun createStudent(
        @Valid @RequestBody
        newStudentDto: NewStudentDto
    ): StudentDto {
        return studentService.saveStudent(newStudentDto)
    }

    @PostMapping("addAndRelate")
    fun addStudentAndRelate(
        @Valid @RequestBody
        newStudentDto: NewStudentDto,
        @Size(min = 1)
        @RequestParam
        courseIds: Set<Long>
    ): StudentDto {
        return studentService.saveStudentAndRelate(newStudentDto, courseIds)
    }

    @GetMapping("find")
    fun findStudent(
        @Valid @RequestBody
        studentDto: StudentDto
    ): StudentDto? {
        return studentService.findStudent(studentDto)
    }

    @GetMapping("findByEmail")
    fun findByEmail(
        @NotBlank(message = "Email is mandatory")
        @Email
        @RequestParam
        email: String
    ): StudentDto? {
        return studentService.findByEmail(email)
    }

    @PutMapping("update")
    fun update(
        @Valid @RequestBody
        studentDto: StudentDto
    ): StudentDto {
        return studentService.update(studentDto)
    }

    @PutMapping("relateToCourses")
    fun relateToCourses(
        @Valid @RequestBody
        studentDto: StudentDto,
        @Size(min = 1)
        @RequestParam
        courseIds: Set<Long>
    ): StudentDto {
        return studentService.checkAndRelateToCourses(studentDto, courseIds)
    }

    @DeleteMapping("delete")
    fun delete(
        @Valid @RequestBody
        studentDto: StudentDto
    ) {
        studentService.delete(studentDto)
    }

    @DeleteMapping("deleteByEmail")
    fun deleteByEmail(
        @Valid @RequestBody
        studentDto: StudentDto
    ) {
        studentService.deleteByEmail(studentDto.email)
    }
}
