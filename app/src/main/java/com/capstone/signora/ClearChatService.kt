package com.capstone.signora

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class ClearChatService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private val firestore = FirebaseFirestore.getInstance()

    private val clearChatRunnable = object : Runnable {
        override fun run() {
            clearFirestorePosts()
            handler.postDelayed(this, 21600000) // I set for 6 hours per clear chat
        }
    }

    private fun clearFirestorePosts() {
        firestore.collection("posts")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    firestore.collection("posts").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("ClearChatService", "Pembersihan Chat!!!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("ClearChatService", "Gagal Mengahapus Chat/Dokumen", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("ClearChatService", "Gagal Mendapatkan Dokumen: ", e)
            }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.post(clearChatRunnable)
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacks(clearChatRunnable)
        super.onDestroy()
    }
}