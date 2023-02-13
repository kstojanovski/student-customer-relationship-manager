package org.acme.scrm.exception.handler

import org.acme.scrm.exception.StudentAlreadyExistsException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class StudentControllerExceptionHandler : ControllerExceptionHandler() {

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(StudentAlreadyExistsException::class)
    fun handleStudentAlreadyExistsException(e: StudentAlreadyExistsException): String {
        return errorMessage(e)
    }
}
