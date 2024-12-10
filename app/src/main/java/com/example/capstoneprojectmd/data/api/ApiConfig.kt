package com.example.capstoneprojectmd.data.api

import com.dicoding.myapplicationcapstone.data.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ApiConfig {
    // Base URL untuk Google Gemini AI API
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/"

    // Instance ApiService
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createOkHttpClient())
            .build()
            .create(ApiService::class.java)
    }

    // Membuat OkHttpClient dengan Authorization header
    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor())
            .build()
    }

    // Interceptor untuk menambahkan Authorization header ke setiap request
    private class AuthorizationInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val apiKey = "AIzaSyD8hb1RoDGCzvQtZccNL634gXculI3-X6I" // Ganti dengan API Key Anda
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $apiKey")
                .build()
            return chain.proceed(newRequest)
        }
    }
}
