package com.example.sneaknest.utils

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthUtils {

    /** user uid */
    val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val currentUserLoginEmail: String
        get() = FirebaseAuth.getInstance().currentUser?.email ?: ""
}
