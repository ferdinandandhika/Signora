package com.capstone.signora

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Set the correct Firebase Database URL by Muhammad Adi Kurnianto
        val database = FirebaseDatabase.getInstance("https://signora-e8d6b-default-rtdb.asia-southeast1.firebasedatabase.app")
        database.setPersistenceEnabled(true)
    }
}