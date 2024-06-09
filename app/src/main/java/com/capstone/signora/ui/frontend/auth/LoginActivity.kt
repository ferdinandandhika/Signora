package com.capstone.signora.ui.frontend.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.capstone.signora.R
import com.capstone.signora.ui.frontend.home.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        testFirestoreQuery()

        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val loginButton: AppCompatButton = findViewById(R.id.login_button)
        val daftarButton: TextView = findViewById(R.id.button_register)
        val showPasswordButton: ImageButton = findViewById(R.id.show_password_button)

        daftarButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val usernameOrEmail = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(usernameOrEmail, password)
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User is signed in by Muhammad Adi Kurnianto
            Log.d("LoginActivity", "User is signed in: ${user.uid}")
        } else {
            // No user is signed in by Muhammad Adi Kurnianto
            Log.d("LoginActivity", "No user is signed in")
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
    }

    private fun loginUser(usernameOrEmail: String, password: String) {
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(usernameOrEmail).matches()) {
            auth.signInWithEmailAndPassword(usernameOrEmail, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            val email = user.email
                            fetchUserNameAndSave(user.uid, email) {
                                startMainActivity()
                            }
                        } else {
                            startMainActivity()
                        }
                    } else {
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            getEmailFromUsername(usernameOrEmail) { email ->
                if (email != null) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                if (user != null) {
                                    fetchUserNameAndSave(user.uid, email) {
                                        startMainActivity()
                                    }
                                } else {
                                    startMainActivity()
                                }
                            } else {
                                Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun fetchUserNameAndSave(uid: String, email: String?, callback: () -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val name = document.getString("name")
                    updateSharedPreferences(name, email)
                    callback()
                } else {
                    Log.d("LoginActivity", "No such document")
                    callback()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("LoginActivity", "get failed with ", exception)
                callback()
            }
    }

    private fun updateSharedPreferences(name: String?, email: String?) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("name", name)
            putString("userEmail", email)
            apply()
        }
        Log.d("LoginActivity", "Updated SharedPreferences with name: $name, email: $email")
    }

    private fun getEmailFromUsername(username: String, callback: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        Log.d("LoginActivity", "Searching for username: $username")
        val query = db.collection("users").whereEqualTo("name", username).limit(1)
        query.get()
            .addOnSuccessListener { documents ->
                if (documents != null && !documents.isEmpty) {
                    val email = documents.documents[0].getString("email")
                    Log.d("LoginActivity", "Email found: $email")
                    callback(email)
                } else {
                    Log.d("LoginActivity", "No such username found. Documents: ${documents.documents}")
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LoginActivity", "Error getting documents: ", exception)
                callback(null)
            }
    }

    private fun testFirestoreQuery() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d("LoginActivity", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LoginActivity", "Error getting documents: ", exception)
            }
    }

    private fun startMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}