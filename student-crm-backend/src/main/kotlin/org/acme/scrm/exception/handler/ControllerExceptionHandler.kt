package org.acme.scrm.exception.handler

import org.acme.scrm.exception.CourseNotFoundException
import org.acme.scrm.exception.EntityAlreadyRelatedException
import org.acme.scrm.exception.StudentNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import javax.validation.ConstraintViolationException

open class ControllerExceptionHandler {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudentNotFoundException::class)
    fun handleStudentNotFoundException(e: StudentNotFoundException): String {
        return errorMessage(e)
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CourseNotFoundException::class)
    fun handleCourseNotFoundException(e: CourseNotFoundException): String {
        return errorMessage(e)
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): String {
        return errorMessage(e)
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityAlreadyRelatedException::class)
    fun handleEntityAlreadyRelatedException(e: EntityAlreadyRelatedException): String {
        return errorMessage(e)
    }

    fun errorMessage(e: Exception): String =
        e::class.java.simpleName + ": Not valid due to validation error: " + e.message
}
