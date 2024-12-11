package com.example.capstoneprojectmd.data.api

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL
import java.util.concurrent.TimeUnit

object ApiConfig {
    private const val TAG = "ApiConfig"
    private const val BASE_URL = "https://us-central1-aiplatform.googleapis.com/"
    private const val TOKEN_URL = "https://gymlens-ml-api-241705916714.asia-southeast2.run.app/generate-token"

    private var currentToken: String? = null
    private var tokenExpiryTime: Long = 0

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val apiService: ApiService = retrofit.create(ApiService::class.java)

    fun getApiService(): ApiService {
        return apiService
    }

    suspend fun getAuthToken(): String? {
        val currentTime = System.currentTimeMillis()
        if (currentToken != null && currentTime < tokenExpiryTime - 5 * 60 * 1000) {
            Log.d(TAG, "Using cached token: $currentToken")
            return currentToken
        }

        return try {
            val response = URL(TOKEN_URL).readText()
            val jsonResponse = JSONObject(response)
            currentToken = jsonResponse.optString("accessToken", null)
            tokenExpiryTime = System.currentTimeMillis() + 3600 * 1000
            Log.d(TAG, "Successfully retrieved token: $currentToken")
            currentToken
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching token: ${e.message}")
            null
        }
    }
}

