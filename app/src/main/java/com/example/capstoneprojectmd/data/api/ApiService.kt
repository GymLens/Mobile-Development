package com.example.capstoneprojectmd.data.api

import com.example.capstoneprojectmd.data.response.ChatContentResponse
import com.example.capstoneprojectmd.data.response.ChatRequest
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("v1/projects/241705916714/locations/us-central1/endpoints/7520317585871601664:generateContent")
    suspend fun generateChatResponse(
        @Header("Authorization") authorization: String,
        @Body chatRequest: ChatRequest
    ): ChatContentResponse
}