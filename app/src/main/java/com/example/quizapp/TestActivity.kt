package com.example.quizapp

import android.animation.Animator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.ArrayMap
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityTestBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class TestActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityTestBinding
    private lateinit var questionList: ArrayList<Question>
    private lateinit var countDownTimer: CountDownTimer
    private var quesNum: Int = 0
    private var score: Int = 0
    private lateinit var fStore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fStore = FirebaseFirestore.getInstance()

        binding.opOne.setOnClickListener(this)
        binding.opTwo.setOnClickListener(this)
        binding.opThree.setOnClickListener(this)
        binding.opFour.setOnClickListener(this)

        questionList = ArrayList()
        getQuestionsList()
    }

    private fun getQuestionsList() {

        questionList.clear()

        fStore.collection("QUIZ").get()
            .addOnSuccessListener {
                var docList = ArrayMap<String, QueryDocumentSnapshot>()
                for(doc: QueryDocumentSnapshot in it){
                    //QueryDocumentSnapshot doc: queryDocumentSnapshot
                    docList[doc.id] = doc
                }
                var questionListDoc: QueryDocumentSnapshot = docList["QUESTION_LIST"]!!
                val count = questionListDoc.getString("COUNT")
                for (i in 0..(Integer.valueOf(count!!))){
                    var quesID = questionListDoc.getString("0" + (i+1).toString() + "_ID")
                    val questionDoc: QueryDocumentSnapshot = docList[quesID]!!
                    questionList.add(
                        Question(
                            questionDoc.getString("QUESTION")!!,
                            questionDoc.getString("A")!!,
                            questionDoc.getString("B")!!,
                            questionDoc.getString("C")!!,
                            questionDoc.getString("D")!!,
                            Integer.valueOf(questionDoc.getString("ANSWER")!!),
                        )
                    )
                }

                setQuestion()
            }.addOnFailureListener {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }

    }

    private fun setQuestion() {
        binding.tvTimer.setText(R.string.timer_time)

        binding.tvQuestion.text = questionList[0].question
        binding.opOne.text = questionList[0].optionA
        binding.opTwo.text = questionList[0].optionB
        binding.opThree.text = questionList[0].optionC
        binding.opFour.text = questionList[0].optionD

        binding.tvQuestionTotal.setText(questionList.size)

        startTimer()
        quesNum = 0
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val time = millisUntilFinished / 1000
                binding.tvTimer.text = time.toString()
            }

            override fun onFinish() {
                changeQuestion()
            }
        }.start()
    }


    override fun onClick(v: View) {
        var selectedOption = 0
        when (v.id) {
            R.id.opOne -> selectedOption = 1
            R.id.opTwo -> selectedOption = 2
            R.id.opThree -> selectedOption = 3
            R.id.opFour -> selectedOption = 4
        }
        countDownTimer.cancel()
        checkAnswer(selectedOption,v)
    }

    private fun checkAnswer(selectedOption: Int, v: View) {
        score
        if (selectedOption == questionList[quesNum].correctAns) {
            (v as Button).setBackgroundColor(Color.GREEN)
            score++
        } else {
            (v as Button).setBackgroundColor(Color.RED)
            when(questionList[quesNum].correctAns){
                1-> binding.opOne.setBackgroundColor(Color.RED)
                2->binding.opTwo.setBackgroundColor(Color.RED)
                3->binding.opThree.setBackgroundColor(Color.RED)
                4->binding.opFour.setBackgroundColor(Color.RED)
            }
        }
        Handler(Looper.getMainLooper()).postDelayed({
            changeQuestion()
            }, 200)

    }

    private fun changeQuestion() {
        if(quesNum < questionList.size - 1){

            quesNum++

            playAnim(binding.tvQuestion, 0, 0)
            playAnim(binding.opOne, 0,1)
            playAnim(binding.opTwo, 0,2)
            playAnim(binding.opThree, 0,3)
            playAnim(binding.opFour, 0,4)

            binding.tvQuestionTotal.setText(questionList.size)
            binding.tvTimer.setText(R.string.timer_time)
            countDownTimer.start()

        }
        else
        {
            val intent = Intent(this, ScoreActivity::class.java)
            intent.putExtra("SCORE", score.toString() + "/" + questionList.size.toString())
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun playAnim(view: View, value: Int, viewNum: Int) {
        view.animate().alpha(value.toFloat()).scaleX(value.toFloat()).setDuration(500)
            .setStartDelay(100).setInterpolator(DecelerateInterpolator())
            .setListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    if (value == 0){
                        when(viewNum){
                            0 -> (view as TextView).text = questionList[quesNum].question
                            1->  (view as Button).text = questionList[quesNum].optionA
                            2->  (view as Button).text = questionList[quesNum].optionB
                            3->  (view as Button).text = questionList[quesNum].optionC
                            4->  (view as Button).text = questionList[quesNum].optionD
                        }
                        if(viewNum != 0){
                            (view as Button).setBackgroundColor(Color.parseColor("2da3e6"))
                        }
                        playAnim(view,1,viewNum)
                    }
                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationRepeat(animation: Animator?) {

                }

            })
    }
}