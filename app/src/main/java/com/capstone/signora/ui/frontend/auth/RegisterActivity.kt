package com.capstone.signora.ui.frontend.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.capstone.signora.R
import com.capstone.signora.ui.frontend.home.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var isPasswordVisible = false
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        progressBar = findViewById(R.id.progressBar)

        val nameEditText: EditText = findViewById(R.id.nama)
        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val registerButton: AppCompatButton = findViewById(R.id.register_button)
        val masukButton: TextView = findViewById((R.id.button_daftar))
        val showPasswordButton: ImageButton = findViewById(R.id.show_password_button)

        masukButton.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        showPasswordButton.setOnClickListener {
            if (isPasswordVisible) {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showPasswordButton.setImageResource(R.drawable.baseline_remove_red_eye_24) // Replace with your icon
            } else {
                passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPasswordButton.setImageResource(R.drawable.eyesblue) // Replace with your icon
            }
            passwordEditText.setSelection(passwordEditText.length())
            isPasswordVisible = !isPasswordVisible
        }

        registerButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showProgressBar()
            registerUser(name, email, password)
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        db.collection("users").whereEqualTo("name", name).get()
            .addOnSuccessListener { documents ->
                hideProgressBar()
                if (!documents.isEmpty) {
                    Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            hideProgressBar()
                            if (task.isSuccessful) {
                                val user = hashMapOf(
                                    "name" to name,
                                    "email" to email
                                )
                                db.collection("users").document(auth.currentUser!!.uid)
                                    .set(user)
                                    .addOnSuccessListener {
                                        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                        val editor = sharedPreferences.edit()
                                        editor.putString("userName", name)
                                        editor.putString("userEmail", email)
                                        editor.apply()

                                        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                hideProgressBar()
                Toast.makeText(this, "Failed to check username: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
}
