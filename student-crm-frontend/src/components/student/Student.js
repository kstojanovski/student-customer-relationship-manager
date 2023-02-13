import React from "react"

const Student = ({student, onSelect}) => {
  return (
    <div className="blueborder" onDoubleClick={(e) => onSelect(e, student.id)}>
        <h3>{student.email}</h3>
        <h4>{student.firstname} {student.lastname}</h4>
    </div>
  )
}

export default Student