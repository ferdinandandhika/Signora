package com.capstone.signora.ui.frontend.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.capstone.signora.R
import de.hdodenhof.circleimageview.CircleImageView
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.capstone.signora.FullScreenImageDialogFragment
import com.capstone.signora.ui.activity.ForumActivity
import com.capstone.signora.CameraX
import com.capstone.signora.ui.frontend.latihan.QuizActivity
import com.capstone.signora.ui.frontend.istilah.IstilahActivity
import com.capstone.signora.ui.frontend.tutorial.TutorialActivity
import com.google.firebase.database.*
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var welcomeTextView: TextView
    private lateinit var profileImageView: CircleImageView
    private val handler = Handler(Looper.getMainLooper())
    private val typingDelay: Long = 150
    private lateinit var profileImageReceiver: BroadcastReceiver
    private lateinit var usernameReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Periksa apakah ini pertama kali pengguna masuk
        val sharedPreferences = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            with(sharedPreferences.edit()) {
                putBoolean("isFirstRun", false)
                apply()
            }
            val intent = Intent(this, TutorialActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Initialize Firebase App Check by Muhammad Adi Kurnianto
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )

        // Inisialisasi Firebase by Muhammad Adi Kurnianto
        FirebaseApp.initializeApp(this)

        welcomeTextView = findViewById(R.id.welcomeText)
        profileImageView = findViewById(R.id.profileImage)
        val welcomeMessage = "Selamat datang!"

        animateText(welcomeMessage)

        // Load user data from Firebase Authentication and Firestore
        loadUserData()
        loadProfileImage()

        // Add this block to handle profile image click by Muhammad Adi Kurnianto
        profileImageView.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val database =
                    FirebaseDatabase.getInstance("https://signora-e8d6b-default-rtdb.asia-southeast1.firebasedatabase.app")
                val databaseRef =
                    database.getReference("users").child(user.uid).child("profileImageUrl")
                databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val imageUrl = snapshot.getValue(String::class.java)
                        if (imageUrl != null) {
                            showImageDialog(imageUrl)
                        } else {
                            Log.d("MainActivity", "Profile image URL is null")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(
                            "MainActivity",
                            "Failed to load profile image URL",
                            error.toException()
                        )
                    }
                })
            }
        }


        // Add this block to handle atas_main click by Muhammad Adi Kurnianto
        val atasMain = findViewById<ConstraintLayout>(R.id.atas_main)
        atasMain.setOnClickListener {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }

        // Add this block to handle istilah CardView click by Muhammad Adi Kurnianto
        val istilahCardView = findViewById<CardView>(R.id.istilah)
        istilahCardView.setOnClickListener {
            val intent = Intent(this, IstilahActivity::class.java)
            startActivity(intent)
        }

        // Add this block to handle latihan harian CardView click by Muhammad Adi Kurnianto
        val latihanCardView = findViewById<CardView>(R.id.latihan)
        latihanCardView.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }

        // Add this block to handle forum CardView click by Muhammad Adi Kurnianto
        val forumCardView = findViewById<CardView>(R.id.forum)
        forumCardView.setOnClickListener {
            val intent = Intent(this, ForumActivity::class.java)
            startActivity(intent)
        }

        // Add this block to handle bottombar_air button click
        val cameraButton = findViewById<ImageButton>(R.id.cameraButton)
        cameraButton.setOnClickListener {
            val intent = Intent(this, CameraX::class.java)
            startActivity(intent)
        }

        // Register broadcast receiver for profile image updates by Muhammad Adi Kurnianto
        profileImageReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val imageUrl = intent?.getStringExtra("imageUrl")
                if (imageUrl != null) {
                    Glide.with(this@MainActivity).load(imageUrl).into(profileImageView)
                    loadProfileImage()
                }
            }
        }
        registerReceiverCompat(
            profileImageReceiver,
            IntentFilter("com.capstone.signora.PROFILE_IMAGE_UPDATED")
        )

        // Register broadcast receiver for username updates by Muhammad Adi Kurnianto
        usernameReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val newUsername = intent?.getStringExtra("newUsername")
                if (newUsername != null) {
                    findViewById<TextView>(R.id.userName).text = newUsername
                }
            }
        }
        registerReceiverCompat(
            usernameReceiver,
            IntentFilter("com.capstone.signora.USERNAME_UPDATED")
        )
    }

    private fun animateText(text: String) {
        welcomeTextView.text = ""
        var index = 0

        val runnable = object : Runnable {
            override fun run() {
                if (index < text.length) {
                    welcomeTextView.append(text[index].toString())
                    index++
                    handler.postDelayed(this, typingDelay)
                }
            }
        }

        handler.post(runnable)
    }

    private fun showImageDialog(imageUrl: String) {
        val fragment = FullScreenImageDialogFragment.newInstance(imageUrl)
        fragment.show(supportFragmentManager, "fullScreenImageDialog")
    }

    private fun loadProfileImage() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val database =
                FirebaseDatabase.getInstance("https://signora-e8d6b-default-rtdb.asia-southeast1.firebasedatabase.app")
            val databaseRef =
                database.getReference("users").child(user.uid).child("profileImageUrl")
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageUrl = snapshot.getValue(String::class.java)
                    if (imageUrl != null) {
                        Log.d("MainActivity", "Profile image URL: $imageUrl")
                        Glide.with(this@MainActivity).load(imageUrl).into(profileImageView)
                        profileImageView.setOnClickListener {
                            showImageDialog(imageUrl)
                        }
                    } else {
                        Log.d("MainActivity", "Profile image URL is null")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MainActivity", "Failed to load profile image URL", error.toException())
                }
            })
        }
    }

    private fun loadUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("name")
                        val email = document.getString("email")
                        findViewById<TextView>(R.id.userName).text = name
                        Log.d("MainActivity", "Loaded user data: name = $name, email = $email")
                    } else {
                        Log.d("MainActivity", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("MainActivity", "get failed with ", exception)
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(profileImageReceiver)
        unregisterReceiver(usernameReceiver)
        handler.removeCallbacksAndMessages(null)
    }

    private fun registerReceiverCompat(receiver: BroadcastReceiver, filter: IntentFilter) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
        }
    }
}