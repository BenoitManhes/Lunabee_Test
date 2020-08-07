package com.example.lunabeeusers.ui.overview

import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
        setupSwipRefreshLayout()
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

    private fun setupSwipRefreshLayout() {
        binding.swiperefresh.setColorSchemeResources(R.color.purple_light)
        binding.swiperefresh.setOnRefreshListener {
            viewModel.refreshData()
        }
    }

    private fun setupObservers() {
        // Observing userList from viewModel
        viewModel.userList.observe(viewLifecycleOwner, Observer {
            it?.let {
                itemAdapter.clear()
                itemAdapter.add(it)
            }
        })

        // Observing statut
        viewModel.statut.observe(viewLifecycleOwner, Observer {
            it?.let {
                when (it) {
                    UserOverviewViewModel.Statut.SUCCESS -> onSuccess()
                    UserOverviewViewModel.Statut.LOADING -> onLoading()
                    UserOverviewViewModel.Statut.ERROR -> onError()
                }
            }
        })
    }

    private fun onError() {
        binding.spinner.visibility = View.GONE
        binding.swiperefresh.isRefreshing = false
    }

    private fun onSuccess() {
        binding.spinner.visibility = View.GONE
        binding.swiperefresh.isRefreshing = false
    }

    private fun onLoading() {
        if (!binding.swiperefresh.isRefreshing) {
            binding.spinner.visibility = View.VISIBLE
        }
    }

}
