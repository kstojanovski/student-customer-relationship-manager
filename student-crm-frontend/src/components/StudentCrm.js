import React from "react"
import { Link } from "react-router-dom"

const StudentCrm = () => {
  return (
    <student-crm>
      <h1 className="blueborder">Welcome to the Student CRM</h1>
      <div className="blueborder">
        <p>Choose one view:</p>
        <ul>
          <li><Link to="/student">Student</Link></li>
          <li><Link to="/course">Course</Link></li>
        </ul>
      </div>      
    </student-crm>
  )
}

export default StudentCrm;