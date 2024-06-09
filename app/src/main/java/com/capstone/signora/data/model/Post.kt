package com.capstone.signora.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Post(
    var title: String = "",
    var content: String = "",
    var profileImageUri: String = "",
    var name: String = "",
    @ServerTimestamp var timestamp: Date? = null
) {
    // No-argument constructor required for Firestore
    constructor() : this("", "", "", "", null)
}
