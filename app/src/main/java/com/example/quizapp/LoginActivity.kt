package com.example.quizapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizapp.databinding.ActivityLoginBinding
import com.example.quizapp.admin.AdminActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fStore: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        fStore = FirebaseFirestore.getInstance()

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        binding.tvForgot.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        binding.btLogin.setOnClickListener {
            if (validateInput()){
                  login()
            }
        }
    }

    private fun login() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Logging In To App .. ")
        progressDialog.setCancelable(false)
        progressDialog.show()
        auth.createUserWithEmailAndPassword(
            binding.etEmail.text.toString(),
            binding.etPassword.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    val user = auth.currentUser
                    checkUser(user!!.uid)
                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressDialog.dismiss()
                }
            }
    }

    private fun checkUser(uid: String) {
        val db = fStore.collection("Users").document(uid)
        db.get().addOnSuccessListener {
            Log.d("TAG", "onSuccess" + it.data)

            if (it.getString("isAdmin") != null) {
                val intent = Intent(applicationContext, AdminActivity::class.java)
                startActivity(intent)
                finish()
            }
            if (it.getString("isStudent") != null) {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun validateInput(): Boolean {
        if (binding.etEmail.text.toString().trim().isEmpty()){
            binding.etEmail.error = "Enter Email"
            return false
        }
        if (binding.etPassword.text.toString().trim().isEmpty()){
            binding.etPassword.error = "Enter Password"
            return false
        }
        return true
    }
}