package com.capstone.signora

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageButton
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UsernameActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_username)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        val usernameEditText: EditText = findViewById(R.id.username_text)
        val changeButton: ImageButton = findViewById(R.id.change_button)

        changeButton.setOnClickListener {
            val newUsername = usernameEditText.text.toString().trim()

            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Please enter a username", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            checkAndChangeUsername(newUsername)
        }
    }

    private fun checkAndChangeUsername(newUsername: String) {
        db.collection("users").whereEqualTo("name", newUsername).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(this, "Username already taken", Toast.LENGTH_SHORT).show()
                } else {
                    updateUsername(newUsername)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to check username: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUsername(newUsername: String) {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            db.collection("users").document(userId)
                .update("name", newUsername)
                .addOnSuccessListener {
                    val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("userName", newUsername)
                    editor.apply()

                    // Send broadcast to notify username change
                    val intent = Intent("com.capstone.signora.USERNAME_UPDATED")
                    intent.putExtra("newUsername", newUsername)
                    sendBroadcast(intent)

                    Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to update username: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}