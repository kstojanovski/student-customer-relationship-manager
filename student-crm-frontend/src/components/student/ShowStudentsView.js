import React from "react"
import StudentsM from "./StudentsM"

const ShowStudentsView = ({students, onSelect}) => {
  return (
    <div>
      <StudentsM students={students} onSelect={onSelect}/>
    </div>
  )
}

export default ShowStudentsView;