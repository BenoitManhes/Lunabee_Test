package com.example.lunabeeusers.data.repository

import com.example.lunabeeusers.data.model.User
import com.example.lunabeeusers.data.remote.UserRemoteDataSource
import com.example.lunabeeusers.utils.Constant
import com.example.lunabeeusers.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) {

    fun getUsers() = performGetOperation(
        networkCall = { remoteDataSource.getAllUsers() }
    )

    fun getUsersPage(page: Int): Flow<Resource<List<User>>> = performGetOperation(
        networkCall = { remoteDataSource.getUsersByPage(page, Constant.PAGE_SIZE) }
    )

    private fun <T> performGetOperation(
        networkCall: suspend () -> Resource<T>
    ): Flow<Resource<T>> =

        flow {
            emit(Resource.loading())
            val source = networkCall.invoke()
            emit(source)
        }
}