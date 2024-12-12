package com.example.capstoneprojectmd.ui.chatbot

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstoneprojectmd.data.api.ApiConfig
import com.example.capstoneprojectmd.data.model.Chat
import com.example.capstoneprojectmd.data.response.ChatRequest
import com.example.capstoneprojectmd.data.response.Parts
import com.example.capstoneprojectmd.data.response.PartsItem
import com.example.capstoneprojectmd.data.response.SystemInstruction
import com.example.capstoneprojectmd.data.response.GenerationConfig
import com.example.capstoneprojectmd.data.response.toData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatViewModel : ViewModel() {

    private val _chatResponse = MutableLiveData<Chat>()
    val chatResponse: LiveData<Chat> = _chatResponse

    private val _keywordAlert = MutableLiveData<String>()
    val keywordAlert: LiveData<String> = _keywordAlert

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val chatHistory = mutableListOf<Parts>()

    fun sendChatRequest(prompt: String) {
        val isExplanationRequest = prompt.contains("explain", ignoreCase = true) ||
                prompt.contains("tell me more", ignoreCase = true)

        // Menambahkan input ke dalam riwayat chat
        chatHistory.add(Parts(text = prompt))

        if (!isExplanationRequest) {
            _keywordAlert.value = "gunakan kata kunci seperti 'explain'/'tell me more' jika ingin penjelasan."
        } else {
            _keywordAlert.value = ""
        }

        val systemInstruction = SystemInstruction(
            parts = listOf(
                PartsItem(
                    text = if (isExplanationRequest) {
                        "Provide a detailed explanation for the user's query."
                    } else {
                        "Analyze the input text and classify it into one of the following categories strictly: [gym, health, nutrition]. Do not include additional explanations or categories."
                    }
                )
            )
        )

        val chatRequest = ChatRequest(
            contents = com.example.capstoneprojectmd.data.response.Contents(
                role = "user",
                parts = Parts(text = prompt)
            ),
            systemInstruction = systemInstruction,
            generationConfig = GenerationConfig(
                temperature = if (isExplanationRequest) 0.9 else 0.0,
                topP = 1.0,
                topK = 1,
                candidateCount = 1,
                stopSequences = listOf()
            )
        )

        sendRequest(chatRequest)
    }

    private fun sendRequest(chatRequest: ChatRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = withContext(Dispatchers.IO) { ApiConfig.getAuthToken() }
                Log.d("ChatViewModel", "Token: $token")

                if (!token.isNullOrEmpty()) {
                    val chatContentResponse = withContext(Dispatchers.IO) {
                        ApiConfig.getApiService().generateChatResponse(
                            authorization = "Bearer $token",
                            chatRequest = chatRequest
                        )
                    }

                    Log.d("ChatViewModel", "Response: $chatContentResponse")

                    chatContentResponse?.let {
                        _chatResponse.value = it.toData()
                    }
                } else {
                    _error.value = "Failed to retrieve a valid token."
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error occurred."
                Log.e("ChatViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
