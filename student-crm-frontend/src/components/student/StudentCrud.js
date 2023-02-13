import React from "react"

const STUDENT_URL = "http://localhost:8888/api/v1/student/";
const STUDENTS_URL = "http://localhost:8888/api/v1/students/";

export const studentCrud$Create = async (addStudentToState, newStudent) => {
    const res = await fetch(STUDENT_URL + "create",
        {
            method: "POST",
            headers: { "Content-type": "application/json" },
            body: JSON.stringify(newStudent)
        })
    addStudentToState(await res.json())
}

export const studentCrud$Read = () => {
    return (
        <div>StudentCrud$Read</div>
    )
}

export const studentCrud$Update = async (updateStudentToState, selectedStudent) => {
    const res = await fetch(STUDENT_URL + "update",
        {
            method: "PUT",
            headers: { "Content-type": "application/json" },
            body: JSON.stringify(selectedStudent)
        })
    updateStudentToState(await res.json())
}

export const studentCrud$Delete = async (deleteStudentFromState, deletedStudent) => {
    const res = await fetch(STUDENT_URL + "delete",
        {
            method: "DELETE",
            headers: { "Content-type": "application/json" },
            body: JSON.stringify(deletedStudent)
        })
    deleteStudentFromState(deletedStudent.id, res.ok)
}

export const studentsCrud$Read = async () => {
    const res = await fetch(STUDENTS_URL + "getAll")
    return await res.json()
}
