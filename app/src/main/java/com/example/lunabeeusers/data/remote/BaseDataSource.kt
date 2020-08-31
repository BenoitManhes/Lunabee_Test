package com.example.lunabeeusers.data.remote

import com.example.lunabeeusers.utils.Resource
import timber.log.Timber

abstract class BaseDataSource {

    protected suspend fun <T> getResult(call: suspend () -> T): Resource<T> {
        try {
            val response = call()
            if (response != null) {
                return Resource.success(response)
            } else {
                return error("Response null")
            }
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): Resource<T> {
        Timber.d(message)
        return Resource.error("Network call has failed for a following reason: $message")
    }

}