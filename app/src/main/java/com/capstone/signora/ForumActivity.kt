package com.capstone.signora.ui.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.signora.R
import com.capstone.signora.adapter.PostAdapter
import com.capstone.signora.data.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query

class ForumActivity : AppCompatActivity() {
    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextPost: EditText
    private lateinit var buttonPost: Button

    private val firestore = FirebaseFirestore.getInstance()
    private var postsListener: ListenerRegistration? = null

    private var profileImageUrl: String? = null

    private val handler = Handler(Looper.getMainLooper())
    private val clearChatRunnable = object : Runnable {
        override fun run() {
            clearChat()
            handler.postDelayed(this, 60000) // Schedule next clear in 60 seconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseFirestore.getInstance().clearPersistence()
        enableEdgeToEdge()
        setContentView(R.layout.activity_forum)


        // Initialize RecyclerView by Muhammad Adi Kurnianto
        recyclerView = findViewById(R.id.recyclerView)
        postAdapter = PostAdapter(posts, this) // Pass 'this' as the activity
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true // Reverse layout to show items from bottom to top by Muhammad Adi Kurnianto
        layoutManager.stackFromEnd = true // Stack items from end by Muhammad Adi Kurnianto
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = postAdapter

        // Fetch profile image URL
        fetchProfileImageUrl()

        // Fetch posts from Firestore by Muhammad Adi Kurnianto
        fetchPosts()

        // Handle posting new post by Muhammad Adi Kurnianto
        editTextPost = findViewById(R.id.editTextPost)
        buttonPost = findViewById(R.id.buttonPost)
        buttonPost.setOnClickListener {
            val postContent = editTextPost.text.toString().trim()
            if (postContent.isEmpty()) {
                // Tampilkan pesan kesalahan menggunakan Toast
                Toast.makeText(this, "Post content cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fetchUserDetailsAndCreatePost(postContent)
        }

        // Start the clear chat runnable
        handler.postDelayed(clearChatRunnable, 60000) // Start after 60 seconds
    }

    private fun fetchProfileImageUrl() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val database = FirebaseDatabase.getInstance("https://signora-e8d6b-default-rtdb.asia-southeast1.firebasedatabase.app")
            val databaseRef = database.getReference("users").child(user.uid).child("profileImageUrl")
            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    profileImageUrl = snapshot.getValue(String::class.java)
                    Log.d("ForumActivity", "Profile image URL: $profileImageUrl")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ForumActivity", "Failed to load profile image URL", error.toException())
                }
            })
        }
    }

    private fun fetchPosts() {
        postsListener = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING) // Order by timestamp in descending order
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ForumActivity", "Error fetching posts: ${e.message}")
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    posts.clear()
                    for (doc in snapshots) {
                        val post = doc.toObject(Post::class.java)
                        posts.add(post)
                    }
                    postAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(0) // Scroll to the latest post
                    Log.d("ForumActivity", "Posts fetched: ${posts.size}")
                }
            }
    }

    private fun fetchUserDetailsAndCreatePost(postContent: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid ?: return

        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val profileImageUri = profileImageUrl ?: ""
                    val name = document.getString("name") ?: "Unknown User"

                    Log.d("ForumActivity", "Fetched user details: name=$name, profileImageUri=$profileImageUri")

                    val newPost = Post(
                        title = name,
                        content = postContent,
                        profileImageUri = profileImageUri,
                        name = name
                    )
                    Log.d("ForumActivity", "Creating Post: $newPost")
                    createNewPost(newPost)
                } else {
                    Log.e("ForumActivity", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.e("ForumActivity", "Error fetching user details: ${e.message}")
            }
    }

    private fun createNewPost(newPost: Post) {
        firestore.collection("posts")
            .add(newPost)
            .addOnSuccessListener { documentReference ->
                Log.d("ForumActivity", "Post created with ID: ${documentReference.id}")
                editTextPost.text.clear()
                recyclerView.scrollToPosition(0) // Scroll to the latest post after adding
            }
            .addOnFailureListener { e ->
                Log.e("ForumActivity", "Error creating post: ${e.message}")
            }
    }

    private fun clearChat() {
        // Clear local list
        posts.clear()
        postAdapter.notifyDataSetChanged()
        Log.d("ForumActivity", "Chat cleared")

        // Clear Firestore collection
        clearFirestorePosts()
    }

    private fun clearFirestorePosts() {
        firestore.collection("posts")
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    firestore.collection("posts").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("ForumActivity", "DocumentSnapshot successfully deleted!")
                        }
                        .addOnFailureListener { e ->
                            Log.w("ForumActivity", "Error deleting document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("ForumActivity", "Error getting documents: ", e)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        postsListener?.remove()
        handler.removeCallbacks(clearChatRunnable) // Stop the clear chat runnable
    }
}
