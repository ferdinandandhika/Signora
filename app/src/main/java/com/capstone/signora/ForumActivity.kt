package com.capstone.signora.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        initializeComponents()
        fetchProfileImageUrl()
        fetchPosts()
        handlePostCreation()
    }

    private fun initializeComponents() {
        recyclerView = findViewById(R.id.recyclerView)
        editTextPost = findViewById(R.id.editTextPost)
        buttonPost = findViewById(R.id.buttonPost)

        postAdapter = PostAdapter(posts, this)
        recyclerView.layoutManager = LinearLayoutManager(this).apply {
            reverseLayout = true
            stackFromEnd = true
        }
        recyclerView.adapter = postAdapter
    }

    private fun fetchProfileImageUrl() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val database = FirebaseDatabase.getInstance("https://signora-e8d6b-default-rtdb.asia-southeast1.firebasedatabase.app")
            val databaseRef = database.getReference("users").child(user.uid).child("profileImageUrl")
            databaseRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    profileImageUrl = snapshot.getValue(String::class.java)
                    Log.d("ForumActivity", "Profile image URL: $profileImageUrl")
                    postAdapter.updateProfileImageUrl(profileImageUrl)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ForumActivity", "Failed to load profile image URL", error.toException())
                }
            })
        }
    }

    private fun fetchPosts() {
        postsListener = firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ForumActivity", "Error fetching posts: ${e.message}")
                    return@addSnapshotListener
                }

                snapshots?.let {
                    posts.clear()
                    for (doc in it) {
                        val post = doc.toObject(Post::class.java)
                        posts.add(post)
                    }
                    postAdapter.notifyDataSetChanged()
                    recyclerView.scrollToPosition(0)
                    Log.d("ForumActivity", "Posts fetched: ${posts.size}")
                }
            }
    }

    private fun handlePostCreation() {
        buttonPost.setOnClickListener {
            val postContent = editTextPost.text.toString().trim()
            if (postContent.isEmpty()) {
                Toast.makeText(this, "Post content cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            fetchUserDetailsAndCreatePost(postContent)
        }
    }

    private fun fetchUserDetailsAndCreatePost(postContent: String) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.uid?.let { userId ->
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    val profileImageUri = profileImageUrl ?: ""
                    val name = document.getString("name") ?: "Unknown User"

                    val newPost = Post(
                        title = name,
                        content = postContent,
                        profileImageUri = profileImageUri,
                        name = name
                    )
                    createNewPost(newPost)
                }
                .addOnFailureListener { e ->
                    Log.e("ForumActivity", "Error fetching user details: ${e.message}")
                }
        }
    }

    private fun createNewPost(newPost: Post) {
        firestore.collection("posts")
            .add(newPost)
            .addOnSuccessListener { documentReference ->
                Log.d("ForumActivity", "Post created with ID: ${documentReference.id}")
                editTextPost.text.clear()
                recyclerView.scrollToPosition(0)
            }
            .addOnFailureListener { e ->
                Log.e("ForumActivity", "Gagal Membuat Post : ${e.message}")
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        postsListener?.remove()
    }
}