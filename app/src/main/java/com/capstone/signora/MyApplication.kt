package com.capstone.signora

import android.app.Application
import android.content.Intent
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Set the correct Firebase Database URL
        val database = FirebaseDatabase.getInstance("https://signora-e8d6b-default-rtdb.asia-southeast1.firebasedatabase.app")
        database.setPersistenceEnabled(true)

        // Start the ClearChatService
        startService(Intent(this, ClearChatService::class.java))
    }
}