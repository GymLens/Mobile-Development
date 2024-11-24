package com.example.capstoneprojectmd.ui.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.capstoneprojectmd.data.pref.UserModel
import com.example.capstoneprojectmd.data.repository.UserRepository

import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}