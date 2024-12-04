package com.example.capstoneprojectmd.ui.chatbot

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneprojectmd.R

class ChatActivity : AppCompatActivity() {
    private var chatRecyclerView: RecyclerView? = null
    private var messageInput: EditText? = null
    private var sendButton: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)


        sendButton?.setOnClickListener {
            val userMessage = messageInput?.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                ApiClient.classifyText(
                    inputText = userMessage,
                    onSuccess = { result ->
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "Klasifikasi: $result",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onError = { error ->
                        runOnUiThread {
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            } else {
                Toast.makeText(this, "Pesan tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
