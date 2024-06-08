package com.capstone.signora.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.signora.R
import com.capstone.signora.adapter.PostAdapter
import com.capstone.signora.data.api.ForumApiService
import com.capstone.signora.data.model.Post
import com.capstone.signora.data.repository.ForumRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.firebase.auth.FirebaseAuth

class ForumActivity : AppCompatActivity() {
    private lateinit var repository: ForumRepository
    private lateinit var postAdapter: PostAdapter
    private val posts = mutableListOf<Post>()

    private lateinit var recyclerView: RecyclerView
    private lateinit var editTextPost: EditText
    private lateinit var buttonPost: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forum)

        // Set padding to handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        postAdapter = PostAdapter(posts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = postAdapter

        // Initialize Retrofit and Repository
        val retrofit = Retrofit.Builder()
            .baseUrl("https://signora-424214.et.r.appspot.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ForumApiService::class.java)
        repository = ForumRepository(service)

        // Fetch posts from API
        fetchPosts()

        // Handle posting new post
        editTextPost = findViewById(R.id.editTextPost)
        buttonPost = findViewById(R.id.buttonPost)
        buttonPost.setOnClickListener {
            val newPost = Post(title = getCurrentUserName(), content = editTextPost.text.toString())
            Log.d("ForumActivity", "Creating Post: $newPost")
            createNewPost(newPost)
        }
    }

    private fun fetchPosts() {
        repository.getPosts().enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                if (response.isSuccessful) {
                    val postsResponse = response.body()
                    postsResponse?.let {
                        posts.clear()
                        posts.addAll(it)
                        postAdapter.notifyDataSetChanged()
                    }
                    Log.d("ForumActivity", "Posts fetched: ${posts.size}")
                } else {
                    Log.e("ForumActivity", "Failed to fetch posts: ${response.errorBody()?.string()}")
                    // Optionally, show error to user
                }
            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.e("ForumActivity", "Error fetching posts: ${t.message}")
                // Optionally, show error to user
            }
        })
    }

    private fun createNewPost(newPost: Post) {
        repository.createPost(newPost).enqueue(object : Callback<Post> {
            override fun onResponse(call: Call<Post>, response: Response<Post>) {
                if (response.isSuccessful) {
                    val createdPost = response.body()
                    createdPost?.let {
                        posts.add(it)
                        postAdapter.notifyItemInserted(posts.size - 1)
                        recyclerView.scrollToPosition(posts.size - 1)
                        editTextPost.text.clear()
                    }
                    Log.d("ForumActivity", "Post created: $createdPost")
                } else {
                    Log.e("ForumActivity", "Failed to create post: ${response.errorBody()?.string()}")
                    // Optionally, show error to user
                }
            }

            override fun onFailure(call: Call<Post>, t: Throwable) {
                Log.e("ForumActivity", "Error creating post: ${t.message}")
                // Optionally, show error to user
            }
        })
    }

    private fun getCurrentUserName(): String {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.displayName ?: "Unknown User"
    }
}
