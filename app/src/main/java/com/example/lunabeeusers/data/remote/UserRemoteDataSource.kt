package com.example.lunabeeusers.data.remote

import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val client: ApiService
) : BaseDataSource() {

    suspend fun getAllUsers() = getResult { client.getUsers() }
    suspend fun getUsersByPage(page: Int, pageSize: Int) = getResult { client.getUsersPage(page, pageSize) }

}