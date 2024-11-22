package com.example.capstoneprojectmd.data.repository

import android.net.http.HttpException
import android.util.Log
import com.example.capstoneprojectmd.data.pref.UserPreference
import com.example.capstoneprojectmd.data.retrofit.ApiService
import com.example.capstoneprojectmd.data.pref.UserModel
import com.example.capstoneprojectmd.data.response.ErrorResponse
import com.example.capstoneprojectmd.data.response.LoginResponse
import com.example.capstoneprojectmd.data.response.RegisterResponse
import kotlinx.coroutines.flow.Flow
import com.google.gson.Gson
import retrofit2.HttpException

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return try {
            val response = apiService.register(name, email, password)
            Log.d(TAG, "Registration successful. Response: $response")
            response
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)?.message
            } catch (jsonException: Exception) {
                "Unknown error occurred"
            }
            Log.e(TAG, "Registration failed: $errorMessage")
            throw Exception(errorMessage)
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            apiService.login(email, password)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = try {
                Gson().fromJson(errorBody, ErrorResponse::class.java)?.message
            } catch (jsonException: Exception) {
                "Unknown error occurred"
            }
            Log.e(TAG, "Login failed: $errorMessage")
            throw Exception(errorMessage)
        }
    }

    suspend fun saveSession(user: UserModel) {
        try {
            userPreference.saveSession(user)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save session: ${e.message}")
            throw e
        }
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        private const val TAG = "UserRepository"

        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository {
            return instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService).also { instance = it }
            }
        }
    }
}
