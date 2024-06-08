package com.capstone.signora.data.repository

import com.capstone.signora.data.api.ForumApiService
import com.capstone.signora.data.model.Post
import retrofit2.Call

class ForumRepository(private val service: ForumApiService) {
    fun getPosts(): Call<List<Post>> = service.getPosts()
    fun createPost(post: Post): Call<Post> = service.createPost(post)
}
