package com.example.capstoneprojectmd.ui.chatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneprojectmd.data.model.Chat
import com.example.capstoneprojectmd.databinding.ItemMessageBinding

class ChatAdapter(private val chatList: MutableList<Chat>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            if (chat.isFromUser) {
                binding.tvUserMessage.text = chat.message
                binding.tvUserMessage.visibility = View.VISIBLE
                binding.tvBotMessage.visibility = View.GONE
            } else {
                binding.tvBotMessage.text = chat.message
                binding.tvBotMessage.visibility = View.VISIBLE
                binding.tvUserMessage.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size

    fun addChat(chat: Chat) {
        chatList.add(chat)
        notifyItemInserted(chatList.size - 1)
    }
    fun clearChats() {
        chatList.clear()
        notifyDataSetChanged()
    }
}
