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
import androidx.navigation.ui.onNavDestinationSelected
import com.example.lunabeeusers.R
import com.example.lunabeeusers.data.model.User
import com.example.lunabeeusers.databinding.UserOverviewFragmentBinding
import com.example.lunabeeusers.ui.overview.UserOverviewViewModel.Statut
import com.example.lunabeeusers.utils.MarginItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter.Companion.items
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.listeners.ItemFilterListener
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import com.mikepenz.fastadapter.ui.items.ProgressItem
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class UserOverviewFragment : Fragment(), ItemFilterListener<GenericItem> {

    private val viewModel: UserOverviewViewModel by viewModels()
    private lateinit var binding: UserOverviewFragmentBinding
    private lateinit var fastItemAdapter: GenericFastItemAdapter
    private lateinit var footerAdapter: GenericItemAdapter
    private lateinit var endlessScrollListener: EndlessRecyclerOnScrollListener
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(findNavController()) ||
            super.onOptionsItemSelected(item)
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
                endlessScrollListener.enable()
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
        //create fastAdapter which will manage everything
        fastItemAdapter = FastItemAdapter()

        //create FooterAdapter which will manage the progress items
        footerAdapter = items()
        fastItemAdapter.addAdapter(1, footerAdapter)

        //set fastAdapter onClickListener
        fastItemAdapter.onClickListener = { _, _, item, _ ->
            if (item is User) {
                navigateToDetailUser(item)
            }
            false
        }

        //configure the filter
        fastItemAdapter.itemFilter.filterPredicate = { item: GenericItem, constraint: CharSequence? ->
            if (item is User) {
                //return true if we should filter it out
                item.isConcernedByTerm(constraint.toString())
            } else {
                //return false to keep it
                false
            }
        }
        fastItemAdapter.itemFilter.itemFilterListener = this

        // Setup RecyclerView
        binding.usersRv.adapter = fastItemAdapter
        binding.usersRv.layoutManager = LinearLayoutManager(context)
        binding.usersRv.itemAnimator = DefaultItemAnimator()

        // onScroll listener
        endlessScrollListener = object : EndlessRecyclerOnScrollListener() {
            override fun onLoadMore(currentPage: Int) {
                Timber.i("endlessScrollListener onLoadMore, page: ${currentPage}")
                showProgressItemScrolling()
                // Loading new items
                viewModel.loadNextPage()
            }
        }
        binding.usersRv.addOnScrollListener(endlessScrollListener)

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
                Timber.i("Users list updated, size: ${fastItemAdapter.itemCount}")
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

    /**
     * Show a progressItem at the end to display the next page loading
     */
    private fun showProgressItemScrolling() {
        footerAdapter.clear()
        val progressItem = ProgressItem()
        progressItem.isEnabled = false
        footerAdapter.add(ProgressItem())

    }

    override fun itemsFiltered(constraint: CharSequence?, results: List<GenericItem>?) {
        footerAdapter.clear()
        endlessScrollListener.disable()

        Timber.i("itemsFiltered, results.size: ${results!!.size}")

        updateFilteringUi()
    }

    override fun onReset() {}

    /**
     * Show fragment detail of user to display
     * @param user User to diplay in detail
     */
    private fun navigateToDetailUser(user: User) {
        this.findNavController().navigate(UserOverviewFragmentDirections
            .actionUserOverviewFragmentToDetailFragment(user))
    }

}
