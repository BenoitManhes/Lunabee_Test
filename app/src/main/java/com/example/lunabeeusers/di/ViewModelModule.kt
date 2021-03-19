package com.example.lunabeeusers.di

import com.example.lunabeeusers.data.remote.UserRemoteDataSource
import com.example.lunabeeusers.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {

    @Provides
    @ViewModelScoped
    fun provideRepository(remoteDataSource: UserRemoteDataSource) =
        UserRepository(remoteDataSource)
}