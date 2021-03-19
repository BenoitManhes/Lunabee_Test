package com.example.lunabeeusers.ui.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lunabeeusers.data.model.User
import com.example.lunabeeusers.data.repository.UserRepository
import com.example.lunabeeusers.utils.Constant
import com.example.lunabeeusers.utils.Resource
import com.example.lunabeeusers.utils.Resource.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UserOverviewViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    // The internal List<User> that stores the users return by the Api
    private var _allUsersList = ArrayList<User>()

    // The internal List<User> that store new users loaded not yet showing
    private var _newUsers = ArrayList<User>()

    // The internal MutableLiveData List<User> that stores the users to show
    private val _usersListToShow = MutableLiveData<ArrayList<User>>()

    // External MutableLiveData List<User> used to diplay users
    val userList: LiveData<ArrayList<User>>
        get() = _usersListToShow

    private val _statut = MutableLiveData<Status>()
    val statut: LiveData<Status>
        get() = _statut

    // The internal String that stores the search term to filter user
    private var _searchTerm = ""
    val searchTerm: String
        get() = _searchTerm

    private var pageToLoad: Int = 0
    private var maxPageReached = false
    private lateinit var lastTaskStatus: Status

    init {
        refreshData()
    }

    fun refreshData() {
        resetUserList()
        loadNextPage()
    }

    /**
     * Reset users List and load first page
     */
    fun resetUserList() {
        pageToLoad = 0
        maxPageReached = false
        _usersListToShow.value = ArrayList()
        _allUsersList.clear()
        _newUsers.clear()
    }

    fun searchUser(searchTerm: String?) {
        _searchTerm = searchTerm ?: ""
        _usersListToShow.value = getUsersFiltered(_allUsersList)

        if (_usersListToShow.value!!.size < Constant.PAGE_SIZE) {
            loadNextPage()
        }
    }

    fun clearUserFilter() {
        searchUser("")
    }

    private fun getUsersFromApi() {
        viewModelScope.launch {

            repository.getUsers()
                .collect { resource: Resource<List<User>> ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            _allUsersList = resource.data as ArrayList<User>
                        }
                        Status.ERROR -> Timber.d(resource.message)
                    }
                    _statut.value = resource.status
                }
        }
    }

    suspend fun getUsersPageFromApi() {
        repository.getUsersPage(pageToLoad)
            .collect { resource: Resource<List<User>> ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        val result = resource.data as ArrayList<User>
                        if (result.isEmpty() && pageToLoad > 0) {
                            maxPageReached = true
                        } else {
                            _allUsersList.addAll(result)
                            _newUsers.addAll(result)
                        }
                    }
                    Status.ERROR -> Timber.d(resource.message)
                }
                lastTaskStatus = resource.status
                Timber.d("Status: ${resource.status}")
            }
    }

    fun loadNextPage() {
        if (!maxPageReached) {
            viewModelScope.launch {
                _statut.value = Status.LOADING
                _newUsers.clear()
                var newUsersToShow = ArrayList<User>()

                do {
                    pageToLoad = _allUsersList.size.div(Constant.PAGE_SIZE)
                    getUsersPageFromApi()
                    newUsersToShow = getUsersFiltered(_newUsers)
                } while (newUsersToShow.size < Constant.PAGE_SIZE && lastTaskStatus != Status.ERROR && !maxPageReached)

                if (!newUsersToShow.isEmpty()) {
                    _usersListToShow += newUsersToShow
                }
                _statut.value = lastTaskStatus
                _newUsers.clear()
            }
        }
    }

    fun isFilterEmpty(): Boolean = _searchTerm == null || _searchTerm.equals("")

    /**
     * Filtering of a user list whith _searchTerm and set result in this list
     * @param listToFilter list where data will be picked
     */
    private fun getUsersFiltered(listToFilter: ArrayList<User>): ArrayList<User> {
        val result = ArrayList<User>()
        for (user in listToFilter) {
            if (user.isConcernedByTerm(_searchTerm)) {
                result.add(user)
            }
        }
        return result
    }

    operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
        val value = this.value ?: arrayListOf()
        value.addAll(values)
        this.value = value
    }
}
