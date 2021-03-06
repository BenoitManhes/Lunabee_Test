package com.example.lunabeeusers.ui.overview

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lunabeeusers.R
import com.example.lunabeeusers.data.model.User
import com.example.lunabeeusers.data.model.UserItem
import com.example.lunabeeusers.databinding.UserOverviewFragmentBinding
import com.example.lunabeeusers.utils.Constant
import com.example.lunabeeusers.utils.MarginItemDecoration
import com.example.lunabeeusers.utils.Resource.Status
import com.example.lunabeeusers.utils.avatarTransitionName
import com.example.lunabeeusers.utils.nameTransitionName
import com.google.android.material.snackbar.Snackbar
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.FastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericFastItemAdapter
import com.mikepenz.fastadapter.adapters.GenericItemAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter.Companion.items
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil
import com.mikepenz.fastadapter.scroll.EndlessRecyclerOnScrollListener
import com.mikepenz.fastadapter.ui.items.ProgressItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.user_overview_fragment.*
import timber.log.Timber

@AndroidEntryPoint
class UserOverviewFragment : Fragment() {

    private val viewModel: UserOverviewViewModel by viewModels()
    private lateinit var binding: UserOverviewFragmentBinding
    private lateinit var fastItemAdapter: GenericFastItemAdapter
    private lateinit var footerAdapter: GenericItemAdapter
    private lateinit var linearlayoutManager: LinearLayoutManager
    private lateinit var endlessScrollListener: EndlessRecyclerOnScrollListener
    private lateinit var searchView: SearchView

    //animation
    private lateinit var slideInTop: Animation
    private lateinit var slideOutTop: Animation

    private var lastClickTime: Long = 0
    private var isOnPause = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = UserOverviewFragmentBinding.inflate(inflater)
        setHasOptionsMenu(true)
        sharedElementReturnTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isOnPause = false

        setupRecyclerView()
        setupSwipRefreshLayout()
        setupListener()
        setupObservers()
        setupAnimations()

        // When user hits back button transition takes backward
        postponeEnterTransition()
        binding.usersRv.doOnPreDraw {
            startPostponedEnterTransition()
        }
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

    override fun onPause() {
        super.onPause()
        isOnPause = true
    }

    private fun setupSearchView(item: MenuItem) {
        // Setup searchView
        searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchUser(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!isOnPause) {
                    viewModel.searchUser(newText)
                }
                return true
            }
        })

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
    }

    private fun setupRecyclerView() {
        //create fastAdapter which will manage everything
        fastItemAdapter = FastItemAdapter()

        //create FooterAdapter which will manage the progress items
        footerAdapter = items()
        fastItemAdapter.addAdapter(1, footerAdapter)

        //set fastAdapter onClickListener
        fastItemAdapter.onClickListener = { view, _, item, _ ->
            val now = System.currentTimeMillis()
            //avoid fast taps crash
            if (now - lastClickTime > Constant.CLICK_INTERVAL) {
                lastClickTime = now
                if (item is UserItem) {
                    navigateToDetailUser(item, view)
                }
            }
            false
        }

        // Setup RecyclerView
        binding.usersRv.adapter = fastItemAdapter
        linearlayoutManager = LinearLayoutManager(context)
        binding.usersRv.layoutManager = linearlayoutManager
        binding.usersRv.itemAnimator = DefaultItemAnimator()

        // onScroll listener
        setupScrollListener()

        // Add Decorator to RecyclerView
        binding.usersRv.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen.padding_short).toInt()
            )
        )
    }

    private fun setupSwipRefreshLayout() {
        binding.swiperefresh.setColorSchemeResources(R.color.primaryColor)
        binding.swiperefresh.setOnRefreshListener {
            viewModel.resetUserList()
            endlessScrollListener.resetPageCount(0)
        }
    }

    private fun setupListener() {
        binding.tryAgain.setOnClickListener {
            viewModel.refreshData()
        }

        binding.goTopBtn.setOnClickListener {
            //avoid to long smooth scrolling
            if (linearlayoutManager.findFirstVisibleItemPosition() > Constant.MAX_ITEMS_SMOOTH_SCROLLING) {
                binding.usersRv.scrollToPosition(Constant.MAX_ITEMS_SMOOTH_SCROLLING)
            }
            binding.usersRv.smoothScrollToPosition(0)
        }
    }

    private fun setupObservers() {
        // Observing userList from viewModel
        viewModel.userList.observe(viewLifecycleOwner, Observer {
            it?.let {
                setUserItem(it)
                updateFilteringUi()
                updateFilterHighLighting()
            }
        })

        // Observing statut
        viewModel.statut.observe(viewLifecycleOwner, Observer {
            it?.let {
                updateStatutUi(it)
                updateFilteringUi()
            }
        })
    }

    /**
     * Handle ui element visibility according to statut
     */
    private fun updateStatutUi(status: Status) {
        val nothingToShow = fastItemAdapter.itemAdapter.adapterItemCount == 0

        updateProgress()
        when (status) {
            Status.LOADING -> {
                //synchronization elements
                binding.syncFailedIv.isVisible = false
                binding.syncFailedTv.isVisible = false
                binding.tryAgain.isVisible = false
            }

            Status.SUCCESS -> {
                //synchronization elements
                binding.syncFailedIv.isVisible = false
                binding.syncFailedTv.isVisible = false
                binding.tryAgain.isVisible = false
                //stop refresh spinner
                binding.swiperefresh.isRefreshing = false
            }

            Status.ERROR -> {
                //stop refresh spinner
                binding.swiperefresh.isRefreshing = false

                //synchronization elements
                if (nothingToShow) {
                    binding.syncFailedIv.isVisible = true
                    binding.syncFailedTv.isVisible = true
                    binding.tryAgain.isVisible = true
                } else {
                    showSnackBar(R.string.sync_failed)
                }
            }
        }
    }

    /**
     * Handle ui element visibility according to filtering result
     */
    private fun updateFilteringUi() {
        val isfilterResultEmpty = fastItemAdapter.itemCount == 0 && !viewModel.isFilterEmpty()
        binding.noResultIv.isVisible = isfilterResultEmpty && viewModel.statut.value == Status.SUCCESS
        binding.noResultText.isVisible = isfilterResultEmpty && viewModel.statut.value == Status.SUCCESS
    }

    /**
     * Handle highlinghting of the text matching with filter term
     */
    private fun updateFilterHighLighting() {
        val term = viewModel.searchTerm
        for (item: GenericItem in fastItemAdapter.itemAdapter.itemList.items) {
            if (item is UserItem) {
                item.highlightTerm = term!!
            }
        }

        fastItemAdapter.notifyDataSetChanged()
    }

    private fun showSnackBar(messageSrc: Int) {
        val snackbar = Snackbar.make(binding.root, messageSrc, Snackbar.LENGTH_SHORT)
        snackbar.view.background = ContextCompat.getDrawable(requireContext(), R.drawable.background_littlebutton)
        snackbar.setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryTextColor))
        snackbar.show()
    }

    /**
     * Show fragment detail of user to display
     * @param item User item to diplay in detail
     * @param view View containing element for shared element transition
     */
    private fun navigateToDetailUser(item: UserItem, view: View?) {
        if (view != null) {
            val extra = FragmentNavigatorExtras(
                item.getViewHolder(view).userAvatarIv to avatarTransitionName(item.user),
                item.getViewHolder(view).nameTv to nameTransitionName(item.user)
            )

            this.findNavController().navigate(
                UserOverviewFragmentDirections.actionUserOverviewFragmentToDetailFragment(item.user),
                extra)
        } else {
            this.findNavController().navigate(UserOverviewFragmentDirections
                .actionUserOverviewFragmentToDetailFragment(item.user))
        }
    }

    /**
     * Convert a User list in UserItem list and fill the itemAdapter
     *
     * @param userList User list to diplay in the recyclerView
     */
    private fun setUserItem(userList: ArrayList<User>) {
        // Creating the UserItems
        val userItemList = ArrayList<UserItem>()
        for (user: User in userList) {
            userItemList.add(UserItem(user))
        }

        // Add UserItems in the itemAdapter with DiffUtil
        FastAdapterDiffUtil[fastItemAdapter.itemAdapter] = userItemList
        Timber.d("After setUserItem, item count:${fastItemAdapter.adapterItemCount}")
        setupScrollListener()
    }

    private fun setupScrollListener() {
        endlessScrollListener = object : EndlessRecyclerOnScrollListener(footerAdapter) {
            override fun onLoadMore(currentPage: Int) {
                Timber.i("endlessScrollListener onLoadMore, page: $currentPage")
                viewModel.loadNextPage()
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (linearlayoutManager.findFirstVisibleItemPosition() == 0 || dy > 0) {
                    hideGoTopBtn()
                } else if (dy < -10) {
                    showGoTopBtn()
                }
            }
        }
        binding.usersRv.clearOnScrollListeners()
        binding.usersRv.addOnScrollListener(endlessScrollListener)
    }

    private fun setupAnimations() {
        slideInTop = AnimationUtils.loadAnimation(context, R.anim.slide_in_top)
        slideOutTop = AnimationUtils.loadAnimation(context, R.anim.slide_out_top)
    }

    private fun hideGoTopBtn() {
        if (binding.goTopBtn.visibility != View.INVISIBLE) {
            binding.goTopBtn.startAnimation(slideOutTop)
            binding.goTopBtn.visibility = View.INVISIBLE
        }
    }

    private fun showGoTopBtn() {
        if (binding.goTopBtn.visibility != View.VISIBLE) {
            binding.goTopBtn.visibility = View.VISIBLE
            binding.goTopBtn.startAnimation(slideInTop)
        }
    }

    private fun updateProgress() {
        footerAdapter.clear()
        if (viewModel.statut.value == Status.LOADING && !swiperefresh.isRefreshing) {
            val progressItem = ProgressItem()
            progressItem.isEnabled = false
            footerAdapter.add(ProgressItem())
        }
    }

}
