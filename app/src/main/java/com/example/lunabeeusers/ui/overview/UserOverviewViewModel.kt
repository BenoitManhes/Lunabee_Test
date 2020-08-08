package com.example.lunabeeusers.ui.overview

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lunabeeusers.data.model.User
import com.example.lunabeeusers.data.repository.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

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

    // Coroutines
    private var viewModelJob = Job()
    private var coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    init {
        getUsersFromApi()
    }

    fun refreshData() {
        getUsersFromApi()
    }

    private fun getUsersFromApi() {
        coroutineScope.launch {
            try {
                _statut.value = Statut.LOADING
                var listResult = repository.getUsers()
                Timber.i("Users Loaded with success")
                _usersList.value = listResult
                _statut.value = Statut.SUCCESS

            } catch (t: Throwable) {
                Timber.i("Fail to load users")
                t.printStackTrace()
                _statut.value = Statut.ERROR
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    enum class Statut {
        LOADING,
        SUCCESS,
        ERROR
    }
}
