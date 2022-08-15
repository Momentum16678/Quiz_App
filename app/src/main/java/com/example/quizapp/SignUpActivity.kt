package com.example.quizapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivitySignUpBinding
import com.example.quizapp.admin.AdminActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        fStore = FirebaseFirestore.getInstance()

        binding.btBack.setOnClickListener {
            finish()
        }

        binding.rbStudent.setOnCheckedChangeListener { buttonView, _ ->
            if (buttonView.isChecked) {
                binding.rbAdmin.isChecked = false
            }
        }

        binding.rbAdmin.setOnCheckedChangeListener { buttonView, _ ->
            if (buttonView.isChecked) {
                binding.rbStudent.isChecked = false
            }
        }

        binding.btSignUp.setOnClickListener {

            if(validate()) {
                signUp()
            }
        }

    }

    private fun validate(): Boolean {
        if (binding.etName.text.toString().trim().isEmpty()){
            binding.etName.error = "Enter Name"
            return false
        }
        if (binding.etMail.text.toString().trim().isEmpty()){
            binding.etMail.error = "Enter Email"
            return false
        }
        if (binding.etPass.text.toString().trim().isEmpty()){
            binding.etPass.error = "Enter Password"
            return false
        }
        if (binding.etPass2.text.toString().trim().isEmpty()){
            binding.etPass2.error = "Enter Password"
            return false
        }
        if (binding.etPass2.text.toString().trim().compareTo(binding.etPass2.text.toString().trim()) != 0 ){
            Toast.makeText(this, "Passwords Are Not The Same", Toast.LENGTH_SHORT).show()
            return false
        }
        if(!(binding.rbAdmin.isChecked || binding.rbStudent.isChecked)){
            Toast.makeText(this, "Select Account Type", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    private fun signUp() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Signing up.. ")
        progressDialog.setCancelable(false)
        progressDialog.show()
        auth.createUserWithEmailAndPassword(
            binding.etMail.text.toString().trim(),
            binding.etPass.text.toString().trim()
         )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    if(binding.rbAdmin.isChecked) {
                        val intent = Intent(applicationContext, AdminActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    if(binding.rbStudent.isChecked) {
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    val user = auth.currentUser
                    Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
                    val db = fStore.collection("Users").document(user!!.uid)
                    val userInfo = hashMapOf(
                        "fullName" to binding.etName.text.toString(),
                        "Password" to binding.etPass.text.toString(),
                         if(binding.rbAdmin.isChecked) {
                             "isAdmin" to "1"
                         }else {
                             "isStudent" to "1"
                         }
                    )
                    db.set(userInfo)
                 }
                else {
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                }
            }
       }
}