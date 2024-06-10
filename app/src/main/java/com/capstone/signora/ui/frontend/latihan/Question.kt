package com.capstone.signora.ui.frontend.latihan

data class Question(
    val id: Int,
    val questionText: String,
    val image: Int,
    val alternatives: ArrayList<String>,
    val correctAnswerIndex: Int,
    val isVideo: Boolean = false,
    val mediaUri: String? = null
)
