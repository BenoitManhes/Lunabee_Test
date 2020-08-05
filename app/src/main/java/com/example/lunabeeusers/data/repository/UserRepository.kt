package com.example.lunabeeusers.data.repository

import com.example.lunabeeusers.data.remote.ApiService
import com.example.lunabeeusers.data.remote.UsersApi

class UserRepository {
    var client: ApiService = UsersApi.retrofitService

    fun getUsers() = client.getUsers()
}