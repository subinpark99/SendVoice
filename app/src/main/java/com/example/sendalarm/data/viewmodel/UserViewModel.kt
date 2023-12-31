package com.example.sendalarm.data.viewmodel

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sendalarm.data.entity.User
import com.example.sendalarm.data.repository.UserRepository
import com.example.sendalarm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repo: UserRepository) : ViewModel() {

    private val _signInState = MutableLiveData<Resource<User>>()
    val signInState: LiveData<Resource<User>> = _signInState

    fun loginWithKakao() =
        viewModelScope.launch {
            repo.loginWithKakao({
                _signInState.postValue(Resource.Success(it))
            }, {
                _signInState.postValue(Resource.Error(it))
            })
        }

    fun getUserList(userEmail: String): MutableLiveData<MutableList<User>?> {
        return repo.getUserList(userEmail)
    }

    fun logout() {
        return repo.logout()
    }

    fun sendKakaoLink(username: String) {
        viewModelScope.launch {
            repo.sendKakaoLink(username)
        }
    }
}
