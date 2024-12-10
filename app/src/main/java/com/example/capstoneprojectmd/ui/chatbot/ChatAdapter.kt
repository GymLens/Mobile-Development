package com.example.capstoneprojectmd.ui.chatbot

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneprojectmd.databinding.ItemChatMessageBinding
import com.example.capstoneprojectmd.model.Chat

class ChatAdapter : ListAdapter<Chat, ChatAdapter.ChatViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return !(oldItem.prompt != newItem.prompt || oldItem.bitmap != newItem.bitmap || oldItem.isFromUser != newItem.isFromUser)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = getItem(position)
        holder.bind(chatItem)
    }

    class ChatViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            if (chat.isFromUser) {
                binding.userMessageLayout.visibility = View.VISIBLE
                binding.botMessageLayout.visibility = View.GONE
                binding.tvUserMessage.text = chat.prompt

                if (chat.bitmap != null) {
                    binding.ivUserImage.setImageBitmap(chat.bitmap)
                    binding.ivUserImage.visibility = View.VISIBLE
                } else {
                    binding.ivUserImage.visibility = View.GONE
                }
            } else {
                binding.botMessageLayout.visibility = View.VISIBLE
                binding.userMessageLayout.visibility = View.GONE
                binding.tvBotMessage.text = chat.prompt

                if (chat.bitmap != null) {
                    binding.ivBotImage.setImageBitmap(chat.bitmap)
                    binding.ivBotImage.visibility = View.VISIBLE
                } else {
                    binding.ivBotImage.visibility = View.GONE
                }
            }
        }
    }
}
