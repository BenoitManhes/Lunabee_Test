package com.example.lunabeeusers.data.model

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.lunabeeusers.R
import com.example.lunabeeusers.utils.clearHighligh
import com.example.lunabeeusers.utils.avatarTransitionName
import com.example.lunabeeusers.utils.highlighTerm
import com.example.lunabeeusers.utils.loadCircleImageFromUrl
import com.example.lunabeeusers.utils.nameTransitionName
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

        val nameTv: TextView = view.findViewById(R.id.nameTv)
        val userAvatarIv: ImageView = view.findViewById(R.id.userAvatarIv)
        val genderTv: TextView = view.findViewById(R.id.genreTv)

        override fun bindView(item: UserItem, payloads: List<Any>) {
            nameTv.text = item.user.firstname + " " + item.user.lastname
            nameTv.transitionName = nameTransitionName(item.user)
            genderTv.text = item.user.gender
            userAvatarIv.loadCircleImageFromUrl(item.user.imgSrcUrl)
            userAvatarIv.transitionName = avatarTransitionName(item.user)
            hightLihgtTerm(item.highlightTerm)
        }

        override fun unbindView(item: UserItem) {
            nameTv.text = null
            genderTv.text = null
            userAvatarIv.loadCircleImageFromUrl("")
        }

        fun hightLihgtTerm(term: String) {
            if (term.equals("") || term.equals(" ")) {
                nameTv.clearHighligh()
            } else {
                val prefixs = term.split(" ")

                for (prefix in prefixs) {
                    nameTv.highlighTerm(prefix)
                }
            }
        }
    }
}