package org.acme.scrm.exception.handler

import org.acme.scrm.exception.CourseAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class CourseControllerExceptionHandler : ControllerExceptionHandler() {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CourseAlreadyExistsException::class)
    fun handleCourseAlreadyExistsException(e: CourseAlreadyExistsException): String {
        return errorMessage(e)
    }
}
