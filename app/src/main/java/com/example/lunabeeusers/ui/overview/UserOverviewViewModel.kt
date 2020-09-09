package com.example.lunabeeusers.ui.overview

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lunabeeusers.data.model.User
import com.example.lunabeeusers.data.repository.UserRepository
import com.example.lunabeeusers.utils.Constant
import com.example.lunabeeusers.utils.Resource
import com.example.lunabeeusers.utils.Resource.Status
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

class UserOverviewViewModel @ViewModelInject constructor(
    private val repository: UserRepository
) : ViewModel() {

    // The internal List<User> that stores the users return by the Api
    private var _allUsersList = ArrayList<User>()

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

    init {
        refreshData()
    }

    fun refreshData() {
        resetUserList()
        getUsersPageFromApi()
    }

    /**
     * Reset users List and load first page
     */
    fun resetUserList() {
        pageToLoad = 0
        _usersListToShow.value = ArrayList()
    }

    fun searchUser(searchTerm: String?) {
        if (searchTerm == null) {
            this._searchTerm = ""
        } else {
            this._searchTerm = searchTerm
        }
        applyFilter()
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
                            applyFilter()
                        }
                        Status.ERROR -> Timber.d(resource.message)
                    }
                    _statut.value = resource.status
                }
        }
    }

    fun getUsersPageFromApi() {
        viewModelScope.launch {

            repository.getUsersPage(pageToLoad)
                .collect { resource: Resource<List<User>> ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            _allUsersList.addAll(resource.data as ArrayList<User>)
                            applyFilter()
                        }
                        Status.ERROR -> Timber.d(resource.message)
                    }
                    _statut.value = resource.status
                }
        }
    }

    fun loadNextPage() {
        pageToLoad = _allUsersList.size.div(Constant.PAGE_SIZE)
        Timber.i("loadNextPage(): ${pageToLoad}")
        getUsersPageFromApi()
    }

    fun isFilterEmpty(): Boolean = _searchTerm == null || _searchTerm.equals("")

    private fun applyFilter() {
        val filteredList = ArrayList<User>()
        for (user in _allUsersList) {
            if (user.isConcernedByTerm(_searchTerm)) {
                filteredList.add(user)
            }
        }
        _usersListToShow.value = filteredList
    }

    operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
        val value = this.value ?: arrayListOf()
        value.addAll(values)
        this.value = value
    }
}
