package com.example.lunabeeusers.di

import com.example.lunabeeusers.data.remote.ApiService
import com.example.lunabeeusers.data.remote.UserRemoteDataSource
import com.example.lunabeeusers.data.repository.UserRepository
import com.example.lunabeeusers.utils.Constant
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(moshi: Moshi): Retrofit = Retrofit.Builder()
        .baseUrl(Constant.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    @Provides
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun provideUserRemoteDataSource(apiService: ApiService) =
        UserRemoteDataSource(apiService)

    @Singleton
    @Provides
    fun provideRepository(remoteDataSource: UserRemoteDataSource) =
        UserRepository(remoteDataSource)
}