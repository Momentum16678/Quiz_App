package com.example.quizapp.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.ArrayMap
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizapp.databinding.ActivityAdminQuestionBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class AdminQuestionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminQuestionBinding
    private lateinit var adminQuestionAdapter : AdminQuestionAdapter
    private lateinit var adminQuestionList: ArrayList<AdminQuestionModel>
    private lateinit var fStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adminQuestionRecyclerAdapter()

        fStore = FirebaseFirestore.getInstance()

        loadQuestions()


        binding.btAddQuestion.setOnClickListener {
            val intent = Intent(this, QuestionDetailActivity::class.java)
            intent.putExtra("ACTION", "ADD")
            startActivity(intent)
        }


    }

    private fun loadQuestions() {
        adminQuestionList.clear()

        fStore.collection("QUIZ").get()
            .addOnSuccessListener {
                var docList = ArrayMap<String, QueryDocumentSnapshot>()
                for(doc: QueryDocumentSnapshot in it){
                    docList[doc.id] = doc
                }
                var questionListDoc: QueryDocumentSnapshot = docList["QUESTION_ID"]!!
                val count = questionListDoc.getString("COUNT")
               for (i in 0..(Integer.valueOf(count!!))){
                   var quesID = questionListDoc.getString("0" + (i+1).toString() + "_ID")
                   val questionDoc: QueryDocumentSnapshot = docList[quesID]!!
                   adminQuestionList!!.add(
                       AdminQuestionModel(
                           quesID!!,
                           questionDoc.getString("QUESTION")!!,
                           questionDoc.getString("A")!!,
                           questionDoc.getString("B")!!,
                           questionDoc.getString("C")!!,
                           questionDoc.getString("D")!!,
                           Integer.valueOf(questionDoc.getString("ANSWER")!!),
                   ))

                   adminQuestionAdapter = AdminQuestionAdapter(adminQuestionList)
                   //using constructors in recyclerView Adapters google
                   binding.rvAdminRecyclerView.adapter = adminQuestionAdapter

                }
            }.addOnFailureListener {
               Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun adminQuestionRecyclerAdapter() {
        binding.rvAdminRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onResume() {
        super.onResume()
        adminQuestionAdapter.notifyDataSetChanged()
    }
}