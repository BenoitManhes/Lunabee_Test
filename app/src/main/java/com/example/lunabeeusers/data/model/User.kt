package com.example.lunabeeusers.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @Json(name = "id") val id: Long,
    @Json(name = "first_name") val firstname: String,
    @Json(name = "last_name") val lastname: String,
    @Json(name = "email") val email: String,
    @Json(name = "gender") val gender: String,
    @Json(name = "avatar") val imgSrcUrl: String
) : Parcelable {

    fun isConcernedByTerm(term: String): Boolean =
        (firstname + " " + lastname).contains(term, ignoreCase = true) ||
            (lastname + " " + firstname).contains(term, ignoreCase = true)
}