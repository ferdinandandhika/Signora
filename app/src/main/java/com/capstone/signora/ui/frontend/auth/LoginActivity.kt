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
import com.capstone.signora.ForgetActivity
import com.capstone.signora.R
import com.capstone.signora.ui.frontend.home.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var isPasswordVisible = false
    private lateinit var progressBar: ProgressBar

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.progressBar)

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val emailOrUsernameEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val loginButton: AppCompatButton = findViewById(R.id.login_button)
        val daftarButton: TextView = findViewById(R.id.button_register)
        val showPasswordButton: ImageButton = findViewById(R.id.show_password_button)
        val forgotPasswordButton: TextView = findViewById(R.id.forgot_password_link)
        val googleButton: ImageButton = findViewById(R.id.google_button)

        daftarButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val usernameOrEmail = emailOrUsernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (usernameOrEmail.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showProgressBar()
            loginUser(usernameOrEmail, password)
        }

        forgotPasswordButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgetActivity::class.java)
            startActivity(intent)
        }

        googleButton.setOnClickListener {
            signInWithGoogle()
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

    private fun signInWithGoogle() {
        googleSignInClient.signOut().addOnCompleteListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Log.w("LoginActivity", "Google sign in failed", e)
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("LoginActivity", "signInWithCredential:success")
                    val user = auth.currentUser
                    saveUserToFirestore(user?.uid, user?.displayName ?: "Pengguna Baru", user?.email ?: "")
                } else {
                    Log.w("LoginActivity", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    hideProgressBar()
                }
            }
    }

    private fun saveUserToFirestore(uid: String?, name: String, email: String) {
        if (uid == null) {
            Toast.makeText(this, "User ID is null", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseFirestore.getInstance()
        val user = hashMapOf(
            "name" to name,
            "email" to email
        )

        db.collection("users").document(uid).set(user)
            .addOnSuccessListener {
                Log.d("LoginActivity", "User added to Firestore")
                Toast.makeText(this, "Berhasil Masuk.", Toast.LENGTH_SHORT).show()
                updateSharedPreferences(name, email)
                startMainActivity()
            }
            .addOnFailureListener { exception ->
                Log.e("LoginActivity", "Error adding user to Firestore: ", exception)
                Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                hideProgressBar()
            }
    }

    private fun loginUser(usernameOrEmail: String, password: String) {
        if (usernameOrEmail.contains("@")) {
            auth.signInWithEmailAndPassword(usernameOrEmail, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressBar()
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Berhasil Masuk", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        if (user != null) {
                            fetchUserNameAndSave(user.uid, user.email) {
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
                            hideProgressBar()
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Berhasil Masuk", Toast.LENGTH_SHORT).show()
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
                    hideProgressBar()
                    Toast.makeText(this, "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
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
                    val fetchedEmail = document.getString("email")
                    Log.d("LoginActivity", "Fetched name: $name, email: $fetchedEmail")
                    updateSharedPreferences(name, fetchedEmail)
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
        Log.d("LoginActivity", "Shared Preferences updated with name: $name, email: $email")
    }

    private fun getEmailFromUsername(username: String, callback: (String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("name", username)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(null)
                } else {
                    val email = documents.first().getString("email")
                    callback(email)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("LoginActivity", "Error getting documents: ", exception)
                callback(null)
            }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }
}
