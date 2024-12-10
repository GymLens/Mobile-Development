package com.dicoding.myapplicationcapstone.data.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    // POST request to send data to the Gemini API
    @POST("models/gemini-1.5-flash-latest:generateContent") // This is just an example endpoint, change to actual one
    suspend fun sendRequest(
        @Header("Authorization") token: String,  // Passing API token as header (Bearer token)
        @Body requestBody: RequestBody          // Request body containing the model input
    ): ResponseBody // Return the raw response body (you can parse it accordingly)
}
