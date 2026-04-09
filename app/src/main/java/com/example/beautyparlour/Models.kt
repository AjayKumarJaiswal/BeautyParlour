package com.example.beautyparlour

data class AuthResponse(
    val success: Boolean? = null,
    val token: String? = null,
    val message: String? = null,
    val user: UserData? = null
)

data class UserData(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String
)

data class GoogleLoginRequest(
    val idToken: String
)
