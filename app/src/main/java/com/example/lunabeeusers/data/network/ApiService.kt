package com.example.lunabeeusers.data.network

import com.example.lunabeeusers.data.model.User
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

private const val BASE_URL = "http://server.lunabee.studio:11111/techtest/"

// Moshi Builder
// Used to convert a JSON in a specific object
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Retrofit Builder
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(BASE_URL)
    .build()

interface ApiService {

    @GET("users")
    fun getUsers():
            Deferred<List<User>>
}

object UsersApi{
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}