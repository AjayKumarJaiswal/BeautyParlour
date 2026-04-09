package com.example.beautyparlour

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/signup")
    fun signup(@Body request: SignupRequest): Call<AuthResponse>

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<AuthResponse>

    @POST("api/auth/google")
    fun googleLogin(@Body request: GoogleLoginRequest): Call<AuthResponse>
}
