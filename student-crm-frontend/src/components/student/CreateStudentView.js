import React, { useState } from "react"
import Popup from 'reactjs-popup';
import { studentCrud$Create } from "./StudentCrud";
import validator from "validator"

const CreateStudentView = ({ addStudentToState }) => {
    const [firstname, setFirstname] = useState("")
    const [lastname, setLastname] = useState("")
    const [email, setEmail] = useState("")
    const [firstnameError, setFirstnameError] = useState("")
    const [lastnameError, setLastnameError] = useState("")
    const [emailError, setEmailError] = useState("")

    const onSave = (firstname, lastname, email) => {
        if (!validateNewStudentInput(firstname, lastname, email)) {
            return false;
        }
        studentCrud$Create(addStudentToState, { firstname, lastname, email });
        setFirstname("");
        setLastname("");
        setEmail("");
        return true;
    }
    const onClose = () => {
        setFirstname("");
        setLastname("");
        setEmail("");
        setFirstnameError("");
        setLastnameError("");
        setEmailError("");
    }

    const validateNewStudentInput = (firstname, lastname, email) => {
        if (!firstname || firstname.length > 20) {
            setFirstnameError("Invalid firstname! \n\r Please add firstname with less thant 21 letters.");
            return false;
        }
        if (!lastname || lastname.length > 20) {
            setLastnameError("Invalid lastname! \n\r Please add firstname with less thant 21 letters.");
            return false;
        }
        if (!validator.isEmail(email)) {
            setEmailError("Invalid e-mail format! Please add valid e-mail format.");
            return false;
        }
        return true;
    }

    return (
        <Popup trigger={<button className="button">Create Student</button>} modal>
            {close => (
                <div>
                    <h3>Create Student</h3>
                    <div>
                        <label>Firstname</label>
                        <input type='text' placeholder="Add Student Firstname" value={firstname} onChange={(e) => setFirstname(e.target.value)}></input>
                        {firstnameError ? (<div className="errorMessage">{firstnameError}</div>) : ""}
                    </div>
                    <div>
                        <label>Lastname</label>
                        <input type='text' placeholder="Add Student Lastname" value={lastname} onChange={(e) => setLastname(e.target.value)}></input>
                        {lastnameError ? (<div className="errorMessage">{lastnameError}</div>) : ""}
                    </div>
                    <div>
                        <label>E-Mail</label>
                        <input type="text" placeholder="Add Student E-Mail" value={email} onChange={(e) => setEmail(e.target.value)}></input>
                        {emailError ? (<div className="errorMessage">{emailError}</div>) : ""}
                    </div>
                    <div className="action">
                        <div className="save">
                            <button type="button" onClick={() => { const valid = onSave(firstname, lastname, email); if (valid) close(); }}>save</button>
                        </div>
                        <div className="close">
                            <button type="button" onClick={() => { onClose(); close(); }}>close</button>
                        </div>
                    </div>
                </div>
            )}
        </Popup>
    )
}

export default CreateStudentView