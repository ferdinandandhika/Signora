package com.capstone.signora.ui.frontend.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.capstone.signora.FotoProfilActivity
import com.capstone.signora.FullScreenImageDialogFragment
import com.capstone.signora.PasswordActivity
import com.capstone.signora.R
import com.capstone.signora.UsernameActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserActivity : AppCompatActivity() {
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var profileImageView: ImageView
    private lateinit var profileImageReceiver: BroadcastReceiver
    private lateinit var usernameReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        userNameTextView = findViewById(R.id.userName)
        userEmailTextView = findViewById(R.id.email)
        profileImageView = findViewById(R.id.profileImage)

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "Nama Pengguna")
        val userEmail = sharedPreferences.getString("userEmail", "email")

        Log.d("UserActivity", "userName: $userName, userEmail: $userEmail")

        userNameTextView.text = userName
        userEmailTextView.text = userEmail

        // Load profile image from Firebase by Muhammad Adi Kurnianto
        loadProfileImage()

        // Handle back button click by Muhammad Adi Kurnianto
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        // Handle "Ganti Foto Profil" click by Muhammad Adi Kurnianto
        val fotoProfilCard = findViewById<CardView>(R.id.foto_profil)
        fotoProfilCard.setOnClickListener {
            val intent = Intent(this, FotoProfilActivity::class.java)
            startActivity(intent)
        }

        // Add this block to handle profile image click by Muhammad Adi Kurnianto
        profileImageView.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val database = FirebaseDatabase.getInstance("https://signora-e8d6b-default-rtdb.asia-southeast1.firebasedatabase.app")
                val databaseRef = database.getReference("users").child(user.uid).child("profileImageUrl")
                databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val imageUrl = snapshot.getValue(String::class.java)
                        if (imageUrl != null) {
                            showImageDialog(imageUrl)
                        } else {
                            Log.d("UserActivity", "Profile image URL is null")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("UserActivity", "Failed to load profile image URL", error.toException())
                    }
                })
            }
        }

        // Register broadcast receiver for profile image updates by Muhammad Adi Kurnianto
        profileImageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val imageUrl = intent?.getStringExtra("imageUrl")
                if (imageUrl != null) {
                    Log.d("UserActivity", "Profile image URL received: $imageUrl")
                    Glide.with(this@UserActivity).load(imageUrl).into(profileImageView)
                }
            }
        }
        registerReceiver(profileImageReceiver, IntentFilter("com.capstone.signora.PROFILE_IMAGE_UPDATED"))

        // Register broadcast receiver for username updates by Muhammad Adi Kurnianto
        usernameReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val newUsername = intent?.getStringExtra("newUsername")
                if (newUsername != null) {
                    userNameTextView.text = newUsername
                }
            }
        }
        registerReceiver(usernameReceiver, IntentFilter("com.capstone.signora.USERNAME_UPDATED"))

        // Handle "Ubah Username" click by Muhammad Adi Kurnianto
        val ubahUsernameCard = findViewById<CardView>(R.id.ubah_username)
        ubahUsernameCard.setOnClickListener {
            val intent = Intent(this, UsernameActivity::class.java)
            startActivity(intent)
        }

        // Handle "Ubah Password" click by Muhammad Adi Kurnianto
        val ubahPasswordCard = findViewById<CardView>(R.id.ubah_password)
        ubahPasswordCard.setOnClickListener {
            val intent = Intent(this, PasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadProfileImage() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val database = FirebaseDatabase.getInstance("https://signora-e8d6b-default-rtdb.asia-southeast1.firebasedatabase.app")
            val databaseRef = database.getReference("users").child(user.uid).child("profileImageUrl")
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageUrl = snapshot.getValue(String::class.java)
                    if (imageUrl != null) {
                        Log.d("UserActivity", "Profile image URL: $imageUrl")
                        Glide.with(this@UserActivity).load(imageUrl).into(profileImageView)
                        profileImageView.setOnClickListener {
                            showImageDialog(imageUrl)
                        }
                    } else {
                        Log.d("UserActivity", "Profile image URL is null")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("UserActivity", "Failed to load profile image URL", error.toException())
                }
            })
        }
    }

    private fun showImageDialog(imageUrl: String) {
        val fragment = FullScreenImageDialogFragment.newInstance(imageUrl)
        fragment.show(supportFragmentManager, "fullScreenImageDialog")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(profileImageReceiver)
        unregisterReceiver(usernameReceiver)
    }
}
