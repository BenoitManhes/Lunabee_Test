package com.example.lunabeeusers.ui.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.lunabeeusers.R
import com.example.lunabeeusers.data.model.User

import com.example.lunabeeusers.databinding.UserOverviewFragmentBinding
import com.example.lunabeeusers.utils.MarginItemDecoration
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserOverviewFragment : Fragment() {

    private val viewModel: UserOverviewViewModel by viewModels()
    private lateinit var binding: UserOverviewFragmentBinding
    private lateinit var fastAdapter: FastAdapter<User>
    private val itemAdapter = ItemAdapter<User>()

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
        fastAdapter = FastAdapter.with(itemAdapter)

        binding.usersRv.adapter = fastAdapter
        // Add Decorator to RecyclerView
        binding.usersRv.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.short_margin).toInt()
            )
        )
    }

    private fun setupObservers() {
        // Observing userList from viewModel
        viewModel.userList.observe(viewLifecycleOwner, Observer {
            it?.let {
                itemAdapter.clear()
                itemAdapter.add(it)
            }
        })
    }

}
