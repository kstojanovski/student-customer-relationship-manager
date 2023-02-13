import React from "react"

const SELECTED_STUDENT_CSSCLASSNAME = "selectedSudent";
/**
 * Sets the style of the selected item and removed from the other items.
 * 
 * @param {event object} e 
 */
export const setTheSelectionStyle = (e) => {
  //reset the selection
  for(let i = 0; i < e.currentTarget.parentElement.childNodes.length; i++) {
    e.currentTarget.parentElement.childNodes[i].classList.remove(SELECTED_STUDENT_CSSCLASSNAME);
  }
  //set the selection
  e.currentTarget.classList.add(SELECTED_STUDENT_CSSCLASSNAME);
}

const STUDENT_DIV_ID = "students";
export const resetTheSelectionStyle = () => {
    if (document.getElementById(STUDENT_DIV_ID)) {
        for(let i = 0; i < document.getElementById(STUDENT_DIV_ID).childNodes.length; i++) {
            document.getElementById(STUDENT_DIV_ID).childNodes[i].classList.remove(SELECTED_STUDENT_CSSCLASSNAME);
        }
    }    
}