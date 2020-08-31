package com.example.lunabeeusers.data.model

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.lunabeeusers.R
import com.example.lunabeeusers.utils.clearHighligh
import com.example.lunabeeusers.utils.highlighTerm
import com.example.lunabeeusers.utils.loadImageFromUrl
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

class UserItem(val user: User) : AbstractItem<UserItem.UserViewHolder>() {

    override val type: Int
        get() = R.id.fastadapter_item

    override var identifier: Long = user.id

    override val layoutRes: Int
        get() = R.layout.item_user_list

    var highlightTerm: String = ""

    fun isConcernedByTerm(term: String): Boolean =
        (user.firstname + " " + user.lastname).contains(term, ignoreCase = true) ||
            (user.lastname + " " + user.firstname).contains(term, ignoreCase = true)

    override fun getViewHolder(v: View): UserViewHolder {
        return UserViewHolder(v)
    }

    /**
     * User's ViewHolder
     */
    class UserViewHolder(view: View) : FastAdapter.ViewHolder<UserItem>(view) {

        val firstnameTv: TextView = view.findViewById(R.id.firstnameTv)
        val lastnameTv: TextView = view.findViewById(R.id.lastnameTv)
        val userAvatarIv: ImageView = view.findViewById(R.id.userAvatarIv)

        override fun bindView(item: UserItem, payloads: List<Any>) {
            firstnameTv.text = item.user.firstname
            lastnameTv.text = item.user.lastname
            userAvatarIv.loadImageFromUrl(item.user.imgSrcUrl)
            hightLihgtTerm(item.highlightTerm)
        }

        override fun unbindView(item: UserItem) {
            firstnameTv.text = null
            lastnameTv.text = null
            userAvatarIv.loadImageFromUrl("")
        }

        fun hightLihgtTerm(term: String) {
            if (term.equals("") || term.equals(" ")) {
                firstnameTv.clearHighligh()
            } else {
                val prefixs = term.split(" ")

                for (prefix in prefixs) {
                    firstnameTv.highlighTerm(prefix)
                    lastnameTv.highlighTerm(prefix)
                }
            }
        }
    }
}