package com.example.capstoneprojectmd.data.di

import android.content.Context
import com.example.capstoneprojectmd.data.pref.UserPreference
import com.example.capstoneprojectmd.data.pref.dataStore
import com.example.capstoneprojectmd.data.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}