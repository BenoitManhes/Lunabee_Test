package com.example.lunabeeusers.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.lunabeeusers.R
import com.example.lunabeeusers.data.model.User
import com.example.lunabeeusers.databinding.UserOverviewFragmentBinding
import com.example.lunabeeusers.ui.detail.DetailFragment
import com.example.lunabeeusers.ui.detail.DetailFragmentDirections
import com.example.lunabeeusers.ui.overview.UserOverviewViewModel.Statut
import com.example.lunabeeusers.utils.MarginItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.listeners.ItemFilterListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class UserOverviewFragment : Fragment(), ItemFilterListener<GenericItem> {

    private val viewModel: UserOverviewViewModel by viewModels()
    private lateinit var binding: UserOverviewFragmentBinding
    private lateinit var fastItemAdapter: GenericFastItemAdapter
    private lateinit var searchView: SearchView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = UserOverviewFragmentBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipRefreshLayout()
        setupListener()
        setupObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
        val searchItem = menu.findItem(R.id.share)
        searchItem?.let {
            setupSearchView(searchItem)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun setupSearchView(item: MenuItem) {
        // Setup searchItem expansion
        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            // Remove filter when search action is collapsed
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                viewModel.clearUserFilter()
                return true
            }
        })

        // Setup query user filter
        searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchUser(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchUser(newText)
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        fastAdapter = FastAdapter.with(itemAdapter)

        binding.usersRv.adapter = fastAdapter
        // Add Decorator to RecyclerView
        binding.usersRv.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.item_spacing).toInt()
            )
        )
    }

    private fun setupSwipRefreshLayout() {
        binding.swiperefresh.setColorSchemeResources(R.color.colorBackgroundDark)
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
                FastAdapterDiffUtil[fastItemAdapter.itemAdapter] = it
                updateFilter(viewModel.searchTerm.value)
            }
        })

        // Observing statut
        viewModel.statut.observe(viewLifecycleOwner, Observer {
            it?.let {
                updateStatutUi(it)
            }
        })

        // Observing search term
        viewModel.searchTerm.observe(viewLifecycleOwner, Observer {
            updateFilter(it)
        })
    }

    /**
     * Handle ui element visibility according to statut
     */
    private fun updateStatutUi(statut: Statut) {
        val nothingToShow = (viewModel.userList.value == null)

        // Synchronization elements
        binding.spinner.isVisible = !binding.swiperefresh.isRefreshing && statut.equals(Statut.LOADING)
        binding.syncFailedIv.isVisible = nothingToShow && statut.equals(Statut.ERROR)
        binding.syncFailedTv.isVisible = nothingToShow && statut.equals(Statut.ERROR)
        binding.tryAgain.isVisible = nothingToShow && statut.equals(Statut.ERROR)

        if (!nothingToShow && statut.equals(Statut.ERROR)) {
            showSnackBar(R.string.sync_failed)
        }

        if (!statut.equals(Statut.LOADING)) {
            binding.swiperefresh.isRefreshing = false
        }
    }

    /**
     * Handle ui element visibility according to filtering result
     */
    private fun updateFilteringUi() {
        val isfilterResultEmpty = fastItemAdapter.itemCount == 0
        binding.noResultIv.isVisible = isfilterResultEmpty
        binding.noResultText.isVisible = isfilterResultEmpty
    }

    private fun showSnackBar(messageSrc: Int) {
        val snackbar = Snackbar.make(binding.root, messageSrc, Snackbar.LENGTH_SHORT)
        snackbar.view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorBackgroundDark))
        snackbar.show()
    }

    private fun updateFilter(term: String?) {
        term?.let {
            fastItemAdapter.filter(it)
        }
    }

    override fun itemsFiltered(constraint: CharSequence?, results: List<GenericItem>?) {
        updateFilteringUi()
    }

    override fun onReset() {

    }

}
