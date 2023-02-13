import React from "react"
import Student from "./Student"

const StudentsM = ({ students, onSelect }) => {
    return (
        <div id="students">
            {students.map((student) => (<Student key={student.id} student={student} onSelect={onSelect} />))}
        </div>
    )
}

export default StudentsM