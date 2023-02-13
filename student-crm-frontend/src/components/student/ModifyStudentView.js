import React, { useEffect } from 'react';
import { resetTheSelectionStyle } from "./StudentStyle"
import { studentCrud$Update, studentCrud$Delete } from "./StudentCrud"
const ModifyStudentView = ({selectedStudent, setSelectedStudent, updateStudentToState, deleteStudentFromState}) => {
  let studentId = selectedStudent.id;
  let firstname = selectedStudent.firstname;
  let lastname = selectedStudent.lastname;
  let email = selectedStudent.email;

  const crowbarInitalization = (htmlId, value) => {
    const element = document.getElementById(htmlId);
    if (element) element.value = value;
  }

  if (selectedStudent !== undefined && selectedStudent !== null) {
    if (selectedStudent.firstname !== undefined && selectedStudent.firstname !== null) {
      crowbarInitalization("msFirstname", selectedStudent.firstname);
    }
    if (selectedStudent.lastname !== undefined && selectedStudent.lastname !== null) {
      crowbarInitalization("msLastname", selectedStudent.lastname);
    }
    if (selectedStudent.email !== undefined && selectedStudent.email !== null) {
      crowbarInitalization("msEmail", selectedStudent.email);
    }
  }

  const disableElement = (htmlId) => {
    document.getElementById(htmlId).disabled = true;
  }

  const enableElement = (htmlId) => {
    const element = document.getElementById(htmlId);
    if (element) element.disabled = false;
  }

  if (studentId) {
    enableElement("msSave");
    enableElement("msClear");
    enableElement("msDelete");
    enableElement("msFirstname");
    enableElement("msLastname");
    enableElement("msEmail");
  }

  const onSave = (id, firstname, lastname, email) => {
    if (!id || !firstname || !lastname || !email) {
      return;
    }
    studentCrud$Update(updateStudentToState, { id, firstname, lastname, email, "courses": [] });
  };
  const onClear = () => {
    resetTheSelectionStyle();
    disableAllElements();
    setSelectedStudent({"id": "", "firstname": "", "lastname": "", "email": ""});
  };
  const onDelete = (id, firstname, lastname, email) => {
    console.log("delete");
    if (!id || !firstname || !lastname || !email) {
      return;
    }
    studentCrud$Delete(deleteStudentFromState, { id, firstname, lastname, email, "courses": [] });
    onClear();
  }

  const disableAllElements = () => {
    disableElement("msSave");
    disableElement("msClear");
    disableElement("msDelete");
    disableElement("msFirstname");
    disableElement("msLastname");
    disableElement("msEmail");
  }

  useEffect(() => {disableAllElements();}, [])

  return (
    <div className="modifyStudent">
      <h3>Modify Student</h3>
      <div>
        <input type="hidden" id="studentId" name="custId" value={studentId}/>
        <div style={{ display: "flex" }}>
          <label style={{ flexGrow: 1, flexShrink: 1, flexBasis: "65px" }}>Firstname</label>
          <input id="msFirstname" style={{ flex: 100 }} type='text' placeholder="Add Student Firstname" onChange={(e) => {firstname=e.target.value}}></input>
        </div>
        <div style={{ display: "flex" }}>
          <label style={{ flexGrow: 1, flexShrink: 1, flexBasis: "65px" }}>Lastname</label>
          <input id="msLastname" style={{ flex: 100 }} type='text' placeholder="Add Student Lastname" onChange={(e) => {lastname=e.target.value}}></input>
        </div>
        <div style={{ display: "flex" }}>
          <label style={{ flexGrow: 1, flexShrink: 1, flexBasis: "65px" }}>E-Mail</label>
          <input id="msEmail" style={{ flex: 100 }} type="text" placeholder="Add Student E-Mail" onChange={(e) => {email=e.target.value}}></input>
        </div>
        <div className="action">
          <button id="msSave" type="button" onClick={() => { onSave(studentId, firstname, lastname, email); }}>save</button>
          <button id="msClear" type="button" onClick={() => { onClear(); }}>clear</button>
          <button id="msDelete"  type="button" onClick={() => { onDelete(studentId, firstname, lastname, email); }}>delete</button>
        </div>
      </div>
    </div>
  )
}

export default ModifyStudentView;