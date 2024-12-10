package com.example.capstoneprojectmd.ui.chatbot


import android.graphics.Bitmap
import com.example.capstoneprojectmd.model.Chat
data class ChatState (
    val chatList: MutableList<Chat> = mutableListOf(),
    val prompt: String = "",
    val bitmap: Bitmap? = null
)