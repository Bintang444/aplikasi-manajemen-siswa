package com.example.tugaspkl.model

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val user: User?
)
