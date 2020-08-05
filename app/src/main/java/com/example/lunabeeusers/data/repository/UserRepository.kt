package com.example.lunabeeusers.data.repository

import com.example.lunabeeusers.data.remote.ApiService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val client: ApiService
) {

    fun getUsers() = client.getUsers()
}