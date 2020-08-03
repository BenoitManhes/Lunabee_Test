package com.example.lunabeeusers.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Users(
    val id: Double,
    val firstname: String,
    val lastname: String,
    val email: String,
    val gender: String,
    @Json(name = "avatar") val imgSrcUrl: String
) : Parcelable