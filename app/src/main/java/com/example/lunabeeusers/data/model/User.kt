package com.example.lunabeeusers.data.model

import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.lunabeeusers.R
import com.example.lunabeeusers.utils.loadImageFromUrl
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
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
) : Parcelable, AbstractItem<User.UserViewHolder>() {

    override val type: Int
        get() = R.id.fastadapter_item

    override var identifier: Long = id

    override val layoutRes: Int
        get() = R.layout.item_user_list

    fun isConcernedByTerm(term: String): Boolean =
        (firstname + " " + lastname).contains(term, ignoreCase = true) ||
            (lastname + " " + firstname).contains(term, ignoreCase = true)

    override fun getViewHolder(v: View): UserViewHolder {
        return UserViewHolder(v)
    }

    /**
     * User's ViewHolder
     */
    class UserViewHolder(view: View) : FastAdapter.ViewHolder<User>(view) {

        var firstnameTv: TextView = view.findViewById(R.id.firstnameTv)
        var lastnameTv: TextView = view.findViewById(R.id.lastnameTv)
        var userAvatarIv: ImageView = view.findViewById(R.id.userAvatarIv)

        override fun bindView(item: User, payloads: List<Any>) {
            firstnameTv.text = item.firstname
            lastnameTv.text = item.lastname
            userAvatarIv.loadImageFromUrl(item.imgSrcUrl)
        }

        override fun unbindView(item: User) {
            firstnameTv.text = null
            lastnameTv.text = null
            userAvatarIv.loadImageFromUrl("")
        }
    }
}