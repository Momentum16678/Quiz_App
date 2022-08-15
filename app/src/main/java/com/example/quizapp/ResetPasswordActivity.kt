package com.example.quizapp

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityResetPasswordBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPasswordActivity: AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        binding.btnReset.setOnClickListener {
            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Loading.. ")
            progressDialog.setCancelable(false)
            progressDialog.show()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            if (binding.etEmailAddress.text.toString().isEmpty())
                binding.tvReset.text = "Email is not provided"
            else {
                auth.sendPasswordResetEmail(binding.etEmailAddress.text.toString())
                    .addOnCompleteListener(this) {
                          if(it.isSuccessful){
                              progressDialog.dismiss()
                              binding.tvReset.text = "Reset Password Link mailed"
                          }else{
                              progressDialog.dismiss()
                              binding.tvReset.text = "Reset Password Link could not be sent  "
                          }
                    }
            }
        }
    }
}