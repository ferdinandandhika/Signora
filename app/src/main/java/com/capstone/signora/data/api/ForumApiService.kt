package com.capstone.signora.data.api

import com.capstone.signora.data.model.Post
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ForumApiService {
    @GET("api/forum/posts")
    fun getPosts(): Call<List<Post>>

    @POST("api/forum/posts")
    fun createPost(@Body post: Post): Call<Post>
}