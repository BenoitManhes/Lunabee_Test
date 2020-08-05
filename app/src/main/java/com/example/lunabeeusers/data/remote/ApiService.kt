package com.example.lunabeeusers.data.remote

import com.example.lunabeeusers.data.model.User
import kotlinx.coroutines.Deferred
import retrofit2.http.GET

interface ApiService {

    @GET("users")
    fun getUsers():
        Deferred<List<User>>
}
