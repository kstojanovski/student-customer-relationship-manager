import React from "react"
import { BrowserRouter as Router, Route, Routes } from "react-router-dom"
import StudentCrm from "./components/StudentCrm"
import Header from "./components/Header"
import Footer from "./components/Footer"
import About from "./components/About"
import StudentView from "./components/student/StudentView"
import CourseView from "./components/course/CourseView"

const App = () => {
  return (
    <Router>
      <div className="blueborder">
        <div className="blueborder"><Header /></div>
        <Routes>
          <Route path="/" element={<StudentCrm />} />
          <Route path="/student" element={<StudentView />}></Route>
          <Route path="/course" element={<CourseView />}></Route>
          <Route path="/about" element={<About />}></Route>
        </Routes>
        <div className="blueborder"><Footer /></div>
      </div>
    </Router>
  )
}

export default App;
