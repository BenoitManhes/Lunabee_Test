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
    private val _usersList = MutableLiveData<List<User>>()

    // External MutableLiveData List<User> used to diplay users
    val userList: LiveData<List<User>>
        get() = _usersList

    private val _statut = MutableLiveData<Statut>()

    val statut: LiveData<Statut>
        get() = _statut

    // The internal MutableLiveData String that stores the search term to filter user
    private val _searchTerm = MutableLiveData<String>()

    // External MutableLiveData String used to filter users
    val searchTerm: LiveData<String>
        get() = _searchTerm

    init {
        getUsersFromApi()
    }

    fun refreshData() {
        getUsersFromApi()
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
            try {
                _statut.value = Statut.LOADING
                var listResult = repository.getUsers()
                _usersList.value = listResult
                _statut.value = Statut.SUCCESS

            } catch (t: Throwable) {
                t.printStackTrace()
                _statut.value = Statut.ERROR
            }
        }
    }

    enum class Statut {
        LOADING,
        SUCCESS,
        ERROR
    }
}
