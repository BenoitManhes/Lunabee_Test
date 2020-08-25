package com.example.lunabeeusers.data.repository

import com.example.lunabeeusers.data.model.User
import com.example.lunabeeusers.data.remote.ApiService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val client: ApiService
) {

    suspend fun getUsers(): List<User> {
        return client.getUsers()
    }
}