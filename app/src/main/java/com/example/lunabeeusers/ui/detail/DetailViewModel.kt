package com.example.lunabeeusers.ui.detail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.lunabeeusers.data.model.User

class DetailViewModel @ViewModelInject constructor(
) : ViewModel() {

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User>
        get() = _currentUser

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

}
