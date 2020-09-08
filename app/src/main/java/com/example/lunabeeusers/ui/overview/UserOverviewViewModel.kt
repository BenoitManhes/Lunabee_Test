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

    // The internal MutableLiveData List<User> that stores the users return by the API
    private val _usersList = MutableLiveData<ArrayList<User>>()

    // External MutableLiveData List<User> used to diplay users
    val userList: LiveData<ArrayList<User>>
        get() = _usersList

    private val _statut = MutableLiveData<Status>()

    val statut: LiveData<Status>
        get() = _statut

    // The internal MutableLiveData String that stores the search term to filter user
    private val _searchTerm = MutableLiveData<String>()

    // External MutableLiveData String used to filter users
    val searchTerm: LiveData<String>
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
        _usersList.value = ArrayList()
    }

    fun searchUser(searchTerm: String?) {
        if (searchTerm == null) {
            _searchTerm.value = ""
        } else {
            _searchTerm.value = searchTerm
        }
    }

    fun clearUserFilter() {
        _searchTerm.value = ""
    }

    private fun getUsersFromApi() {
        viewModelScope.launch {

            repository.getUsers()
                .collect { resource: Resource<List<User>> ->
                    when (resource.status) {
                        Status.SUCCESS -> _usersList.value = resource.data as ArrayList<User>
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
                        Status.SUCCESS -> _usersList += resource.data!!
                        Status.ERROR -> Timber.d(resource.message)
                    }
                    _statut.value = resource.status
                }
        }
    }

    fun loadNextPage() {
        pageToLoad = _usersList.value?.size?.div(Constant.PAGE_SIZE) ?: 0
        Timber.i("loadNextPage(): ${pageToLoad}")
        getUsersPageFromApi()
    }

    fun isFilterEmpty(): Boolean = searchTerm.value == null || searchTerm.value.equals("")

    operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
        val value = this.value ?: arrayListOf()
        value.addAll(values)
        this.value = value
    }
}
