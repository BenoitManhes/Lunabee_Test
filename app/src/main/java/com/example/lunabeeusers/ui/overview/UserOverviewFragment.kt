package com.example.lunabeeusers.ui.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.lunabeeusers.R

import com.example.lunabeeusers.databinding.UserOverviewFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserOverviewFragment : Fragment() {

    private val viewModel: UserOverviewViewModel by viewModels()
    private lateinit var binding: UserOverviewFragmentBinding
    private lateinit var adapter: UserListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = UserOverviewFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        adapter = UserListAdapter()
        binding.usersRv.adapter = adapter
        // Add Decorator to RecyclerView
        binding.usersRv.addItemDecoration(
            UserListAdapter.MarginItemDecoration(
                resources.getDimension(R.dimen.short_margin).toInt()
            )
        )
    }

    private fun setupObservers() {
        // Observing userList from viewModel
        viewModel.userList.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })
    }

}
