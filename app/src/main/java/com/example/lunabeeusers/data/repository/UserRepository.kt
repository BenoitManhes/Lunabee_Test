package com.example.lunabeeusers.data.repository

import com.example.lunabeeusers.data.model.User
import com.example.lunabeeusers.data.remote.ApiService
import com.example.lunabeeusers.utils.Constant
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val client: ApiService
) {

    suspend fun getUsers(): List<User> {
        return client.getUsers()
    }

    suspend fun getUsersPage(page: Int): List<User> {
        return client.getUsersPage(page, Constant.PAGE_SIZE)
    }
}