package com.example.quizapp.admin

data class AdminQuestionModel (
     var quesID: String,
     var question: String,
     var optionA: String,
     var optionB: String,
     var optionC: String,
     var optionD: String,
     var correctAns: Int
)