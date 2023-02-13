import React, { useState, useEffect } from "react"
import { studentsCrud$Read } from "./StudentCrud"
import { setTheSelectionStyle } from "./StudentStyle"
import CreateStudentView from "./CreateStudentView"
import ShowStudentsView from "./ShowStudentsView"
import ModifyStudentView from "./ModifyStudentView"

const StudentView = () => {
  const [students, setStudents] = useState([])

  const addStudentToState = (newStudent) => {
    setStudents([...students, newStudent])
  }
  const updateStudentToState = (selectedStudent) => {
    modifyStudentState(students, selectedStudent)
    setStudents([...students])
  }
  const deleteStudentFromState = (deletedStudentId) => {
    removeStudentState(students, deletedStudentId)
    setStudents([...students])
  }
  
  const removeStudentState = async(students, deletedStudentId) => {
    const removeIndex = students.map((student) => student.id).indexOf(deletedStudentId);
    students.splice(removeIndex, 1);
    return students;
  }
  const modifyStudentState = async(students, selectedStudent) => {
    students.forEach(student => {
      if (student.id === selectedStudent.id) {
        student.firstname = selectedStudent.firstname;
        student.lastname = selectedStudent.lastname;
        student.email = selectedStudent.email;
      }
    });
    return students;
  }

  useEffect(() => {
    const getStudents = async () => {
      setStudents(await studentsCrud$Read())
    }
    getStudents()
  }, [])

  const [selectedStudent, setSelectedStudent] = useState({"id": "", "firstname": "", "lastname": "", "email": ""})

  const onSelect = async (e, id) => {
    setTheSelectionStyle(e);
    setSelectedStudent(students.filter(student => student.id === id)[0])
  }

  return (
    <div className="blueborder">
      <h2 className="blueborder">Student View</h2>
      <div className="blueborder">
        <CreateStudentView addStudentToState={addStudentToState} />
      </div>
      <div style={{ display: "flex" }} className="blueborder">
        <div style={{ flex: 3 }} className="blueborder">
          <ShowStudentsView students={students} onSelect={onSelect}/>
        </div>
        <div style={{ flex: 2 }} className="blueborder">
          <ModifyStudentView key={selectedStudent}
            selectedStudent={selectedStudent}
            setSelectedStudent={setSelectedStudent}
            updateStudentToState={updateStudentToState}
            deleteStudentFromState={deleteStudentFromState}
        />
        </div>
      </div>
    </div>
  )
}

export default StudentView;