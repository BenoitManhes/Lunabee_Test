package com.example.lunabeeusers.data.model

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.lunabeeusers.R
import com.example.lunabeeusers.databinding.ItemUserListBinding
import com.example.lunabeeusers.utils.loadImageFromUrl
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    @Json(name = "id") val id: Double,
    @Json(name = "first_name") val firstname: String,
    @Json(name = "last_name") val lastname: String,
    @Json(name = "email") val email: String,
    @Json(name = "gender") val gender: String,
    @Json(name = "avatar") val imgSrcUrl: String
) : Parcelable, AbstractBindingItem<ItemUserListBinding>() {

    override val type: Int
        get() = R.id.fastadapter_item

    override fun bindView(binding: ItemUserListBinding, payloads: List<Any>) {
        binding.firstnameTv.text = firstname
        binding.lastnameTv.text = lastname
        loadImageFromUrl(binding.userAvatarIv, imgSrcUrl)
    }

    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): ItemUserListBinding {
        return ItemUserListBinding.inflate(inflater, parent, false)
    }

    fun isConcernedByTerm(term: String): Boolean =
        (firstname + " " + lastname).contains(term, ignoreCase = true) ||
            (lastname + " " + firstname).contains(term, ignoreCase = true)
}