package com.capstone.signora.ui.frontend.viewmodel

import androidx.lifecycle.ViewModel
import com.capstone.signora.data.repository.ForumRepository

class ForumViewModel : ViewModel() {
    lateinit var repository: ForumRepository
}