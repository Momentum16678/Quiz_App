package com.example.quizapp.admin

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.quizapp.R
import com.example.quizapp.databinding.AdminQuestionItemBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.collections.ArrayList

class AdminQuestionAdapter(
    var adminQuestionList: ArrayList<AdminQuestionModel>
) : RecyclerView.Adapter<AdminQuestionAdapter.AdminQuestionViewHolder>() {

    class AdminQuestionViewHolder(var binding: AdminQuestionItemBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminQuestionViewHolder {
        return AdminQuestionViewHolder(AdminQuestionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: AdminQuestionViewHolder, position: Int) {
        holder.binding.tvAdminQuestion.setText(R.string.admin_question + (position+1))
        holder.binding.ivDelete.setOnClickListener {
            val  deleteQuestionDialog = AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Question")
                .setMessage("Do you want to Delete Question?")
                .setIcon(R.drawable.ic_baseline_delete_24)
                .setPositiveButton("Yes"){ _, _ ->
                  deleteQuestion(position, holder.itemView.context, AdminQuestionAdapter(adminQuestionList))
                }
                .setNegativeButton("No",null)
                .setIcon(R.drawable.ic_baseline_cancel_24)
                .show()

            deleteQuestionDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#2da3e6"))
            deleteQuestionDialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.parseColor("#2da3e6"))

            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            params.setMargins(0,0,50,0)
            deleteQuestionDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, QuestionDetailActivity::class.java)
            intent.putExtra("ACTION", "EDIT")
            intent.putExtra("Q_ID", position)
            holder.itemView.context.startActivity(intent)
        }
    }

    private fun deleteQuestion(position: Int, context: Context, adapter: AdminQuestionAdapter) {

        var fStore: FirebaseFirestore = FirebaseFirestore.getInstance()
        fStore.collection("QUIZ").document(adminQuestionList[position].quesID)
            .delete()
            .addOnSuccessListener{
                var quesDoc = ArrayMap<String, Any>()
                var index = 1
                for (i in 0..adminQuestionList.size){
                    if(i!=position){
                        quesDoc["0" + index.toString() + "_ID"] = adminQuestionList[i].quesID
                        index++
                    }
                }
                quesDoc["COUNT"] = (index-1).toString()

                fStore.collection("QUIZ").document("QUESTION_LIST")
                    .set(quesDoc)
                    .addOnSuccessListener{
                        Toast.makeText(context,"Question Deleted Successfully", Toast.LENGTH_SHORT).show()
                        adminQuestionList.removeAt(position)
                        adapter.notifyDataSetChanged()
                    }.addOnFailureListener{
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener{
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int {
        return adminQuestionList.size
    }
}