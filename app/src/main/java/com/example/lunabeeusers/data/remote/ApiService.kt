package com.example.lunabeeusers.data.remote

import com.example.lunabeeusers.data.model.User
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("users")
    suspend fun getUsers(): List<User>

    @GET("users")
    suspend fun getUsersPage(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): List<User>
}
