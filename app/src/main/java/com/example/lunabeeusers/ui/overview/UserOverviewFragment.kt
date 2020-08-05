package com.example.lunabeeusers.ui.overview

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lunabeeusers.R

import com.example.lunabeeusers.databinding.UserOverviewFragmentBinding

class UserOverviewFragment : Fragment() {

    private val viewModel: UserOverviewViewModel by lazy {
        ViewModelProviders.of(this).get(UserOverviewViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val binding = UserOverviewFragmentBinding.inflate(inflater)

        // Allows Data Binding to Observe LiveData with the lifecycle of this Fragment
        binding.setLifecycleOwner(this)

        // Giving the binding access to the OverviewViewModel
        binding.viewModel = viewModel

        // Giving a UserListAdapter to RecyclerView
        binding.usersRv.adapter = UserListAdapter()
        // Add Decorator to RecyclerView
        binding.usersRv.addItemDecoration(
            UserListAdapter.MarginItemDecoration(
                resources.getDimension(R.dimen.short_margin).toInt()
            )
        )

        return binding.root
    }

}
