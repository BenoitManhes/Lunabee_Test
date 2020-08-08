package com.example.lunabeeusers.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.lunabeeusers.R
import com.example.lunabeeusers.data.model.User
import com.example.lunabeeusers.databinding.UserOverviewFragmentBinding
import com.example.lunabeeusers.ui.overview.UserOverviewViewModel.Statut
import com.example.lunabeeusers.utils.MarginItemDecoration
import com.google.android.material.snackbar.Snackbar
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
        setupListener()
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

    private fun setupListener() {
        binding.tryAgain.setOnClickListener {
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
                updateStatutUi(it)
            }
        })
    }

    /**
     * Handle ui element visibility according to statut
     */
    private fun updateStatutUi(statut: Statut) {
        val nothingToShow = (viewModel.userList.value == null)

        binding.spinner.visibility =
            if (!binding.swiperefresh.isRefreshing && statut.equals(Statut.LOADING)) View.VISIBLE else View.GONE
        binding.syncFailedIv.visibility = if (nothingToShow && statut.equals(Statut.ERROR)) View.VISIBLE else View.GONE
        binding.syncFailedTv.visibility = if (nothingToShow && statut.equals(Statut.ERROR)) View.VISIBLE else View.GONE
        binding.tryAgain.visibility = if (nothingToShow && statut.equals(Statut.ERROR)) View.VISIBLE else View.GONE

        if (!nothingToShow && statut.equals(Statut.ERROR)) {
            Snackbar.make(binding.root, R.string.sync_failed, Snackbar.LENGTH_SHORT).show()
        }

        if (!statut.equals(Statut.LOADING)) {
            binding.swiperefresh.isRefreshing = false
        }
    }

}
