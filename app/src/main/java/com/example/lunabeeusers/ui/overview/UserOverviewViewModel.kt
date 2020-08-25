package com.example.lunabeeusers.ui.overview

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lunabeeusers.data.model.User
import com.example.lunabeeusers.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserOverviewViewModel @ViewModelInject constructor(
    private val repository: UserRepository
) : ViewModel() {

    // The internal MutableLiveData List<User> that stores the users return by the API
    private val _usersList = MutableLiveData<ArrayList<User>>()

    // External MutableLiveData List<User> used to diplay users
    val userList: LiveData<ArrayList<User>>
        get() = _usersList

    private val _statut = MutableLiveData<Statut>()

    val statut: LiveData<Statut>
        get() = _statut

    // The internal MutableLiveData String that stores the search term to filter user
    private val _searchTerm = MutableLiveData<String>()

    // External MutableLiveData String used to filter users
    val searchTerm: LiveData<String>
        get() = _searchTerm

    // Navigation
    private var _navigateToSleepDetail = MutableLiveData<User>()
    val navigateToSleepDetail: LiveData<User>
        get() = _navigateToSleepDetail

    init {
        resetUserList()
    }

    fun refreshData() {
        resetUserList()
    }

    /**
     * Reset users List and load first page
     */
    fun resetUserList() {
        _usersList.value = null
        getUsersPageFromApi(0)
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

    fun onUserCliked(user: User) {
        _navigateToSleepDetail.value = user
    }

    fun doneSleepDetailNavigatided() {
        _navigateToSleepDetail.value = null
    }

    private fun getUsersFromApi() {
        viewModelScope.launch {
            try {
                _statut.value = Statut.LOADING
                var listResult = repository.getUsers()
                _usersList.value = listResult as ArrayList<User>
                _statut.value = Statut.SUCCESS

            } catch (t: Throwable) {
                t.printStackTrace()
                _statut.value = Statut.ERROR
            }
        }
    }

    fun getUsersPageFromApi(page: Int) {
        viewModelScope.launch {
            try {
                _statut.value = Statut.LOADING
                var listResult = repository.getUsersPage(page)
                _usersList += listResult
                _statut.value = Statut.SUCCESS
            } catch (t: Throwable) {
                _statut.value = Statut.ERROR
                t.printStackTrace()
            }
        }
    }

    operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
        val value = this.value ?: arrayListOf()
        value.addAll(values)
        this.value = value
    }

    enum class Statut {
        LOADING,
        SUCCESS,
        ERROR
    }
}
