package com.example.quizapp.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import android.widget.Toast
import com.example.quizapp.R
import com.example.quizapp.databinding.ActivityQuestionDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionDetailBinding
    lateinit var adminQuestionList: ArrayList<AdminQuestionModel>
    lateinit var qStr: String
    lateinit var aStr:String
    lateinit var bStr:String
    lateinit var cStr:String
    lateinit var dStr:String
    lateinit var ansStr:String

    private lateinit var fStore: FirebaseFirestore
    private lateinit var action: String
    private var qID: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fStore = FirebaseFirestore.getInstance()
        action = intent.getStringExtra("ACTION")!!

        if (action.compareTo("EDIT")==0)
        {
            qID = intent.getIntExtra("Q_ID", 0)
            loadData(qID)
            binding.btAddNew.text = R.string.bt_update.toString()
        }
        else {
            binding.btAddNew.text = R.string.bt_add.toString()
        }

        binding.btAddNew.setOnClickListener {
            qStr = binding.etQuestionNew.text.toString()
            aStr = binding.etOptionA.text.toString()
            bStr = binding.etOptionB.text.toString()
            cStr = binding.etOptionC.text.toString()
            dStr = binding.etOptionD.text.toString()
            ansStr = binding.etAnswer.text.toString()

            if(qStr.isEmpty()){
                binding.etQuestionNew.error = "Enter Question"
            }
            if(aStr.isEmpty()){
                binding.etOptionA.error = "Enter Option A"
            }
            if(bStr.isEmpty()){
                binding.etOptionB.error = "Enter Option B"
            }
            if(cStr.isEmpty()){
                binding.etOptionC.error = "Enter Option C"
            }
            if(dStr.isEmpty()){
                binding.etOptionD.error = "Enter Option D"
            }
            if(ansStr.isEmpty()){
                binding.etAnswer.error = "Enter Answer"
            }
            if (action.compareTo("EDIT")==0){
                editQuestion()
            }else {
                addNewQuestion()
            }
         }

    }

    private fun editQuestion() {
        var quesData = ArrayMap<String, Any>()
        quesData["QUESTION"] = qStr
        quesData["A"] = aStr
        quesData["B"] = bStr
        quesData["C"] = cStr
        quesData["D"] = dStr
        quesData["ANSWER"] = ansStr

        fStore.collection("QUIZ").document(adminQuestionList[qID].quesID)
            .set(quesData)
            .addOnSuccessListener {
                Toast.makeText(this, "Question Updated Successfully", Toast.LENGTH_SHORT).show()
                adminQuestionList[qID].question = qStr
                adminQuestionList[qID].optionA = aStr
                adminQuestionList[qID].optionB = bStr
                adminQuestionList[qID].optionC = cStr
                adminQuestionList[qID].optionD = dStr
                adminQuestionList[qID].correctAns = ansStr.toInt()

                finish()
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadData(qID: Int) {
        binding.etQuestionNew.setText(adminQuestionList[qID].question)
        binding.etOptionA.setText(adminQuestionList[qID].optionA)
        binding.etOptionB.setText(adminQuestionList[qID].optionB)
        binding.etOptionC.setText(adminQuestionList[qID].optionC)
        binding.etOptionD.setText(adminQuestionList[qID].optionD)
        binding.etAnswer.setText(adminQuestionList[qID].correctAns.toString())
    }

    private fun addNewQuestion() {
        var quesData = ArrayMap<String, Any>()
        quesData["QUESTION"] = qStr
        quesData["A"] = aStr
        quesData["B"] = qStr
        quesData["C"] = cStr
        quesData["D"] = dStr
        quesData["ANSWER"] = ansStr

        var docId = fStore.collection("QUIZ").document().id

        fStore.collection("QUIZ").document(docId)
            .set(quesData)
            .addOnSuccessListener {
                var quesDoc = ArrayMap<String, Any>()
                quesDoc.put("0" + (adminQuestionList.size.toString() + 1) + "_ID", docId)
                quesDoc.put("COUNT", (adminQuestionList.size.toString() + 1))
                fStore.collection("QUIZ").document("QUESTION_LIST")
                    .update(quesDoc)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Question Added Successfully", Toast.LENGTH_SHORT).show()
                        adminQuestionList.add(
                            AdminQuestionModel(
                            docId,
                            qStr,aStr,bStr,cStr,dStr, ansStr.toInt()
                        ))
                        finish()
                    }.addOnFailureListener{
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener{
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
}