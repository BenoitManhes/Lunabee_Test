package com.example.lunabeeusers.ui.overview

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lunabeeusers.data.model.User
import com.example.lunabeeusers.databinding.ItemUserListBinding
import com.example.lunabeeusers.utils.loadImageFromUrl

class UserListAdapter : ListAdapter<User, UserListAdapter.UserViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup,
        viewType: Int): UserViewHolder {
        return UserViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
    }

    class UserViewHolder(private var binding: ItemUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            user?.let {
                binding.firstnameTv.text = user.firstname
                binding.lastnameTv.text = user.lastname
                loadImageFromUrl(binding.userAvatarIv, user.imgSrcUrl)
            }
        }

        companion object {
            fun from(parent: ViewGroup): UserViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemUserListBinding.inflate(layoutInflater, parent, false)
                return UserViewHolder(binding)
            }
        }
    }

    /**
     * Use to optimize view displaying
     * When data change, allow to update only view related to data updtated
     */
    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id

        }
    }

    /**
     * Custom decorator for the Recyclerview
     */
    class MarginItemDecoration(private val spaceHeight: Int) : RecyclerView.ItemDecoration() {

        /**
         * Setting space between Recycler View’s cell
         */
        override fun getItemOffsets(outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = spaceHeight
                }
                bottom = spaceHeight
            }
        }
    }
}