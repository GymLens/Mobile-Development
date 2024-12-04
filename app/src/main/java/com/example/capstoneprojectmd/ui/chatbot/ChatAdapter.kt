package com.example.capstoneprojectmd.ui.chatbot


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.capstoneprojectmd.R


class ChatAdapter(private val messageList: MutableList<com.example.capstoneprojectmd.model.Message>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    // Inflate the item layout for each message
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    // Bind the message to the view holder
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messageList[position]
        holder.bind(message)
    }

    // Return the total count of messages
    override fun getItemCount(): Int {
        return messageList.size
    }

    // Add new message to the list and notify the adapter
    fun addMessage(message: com.example.capstoneprojectmd.model.Message) {
        messageList.add(message)
        notifyItemInserted(messageList.size - 1)
    }

    // ViewHolder class for each message
    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)

        // Bind data to the view
        fun bind(message: com.example.capstoneprojectmd.model.Message) {
            messageText.text = message.content
            // Apply different styles based on sender type (user or bot)
            if (message.sender == "user") {
                messageText.setBackgroundResource(R.drawable.bg_user_message)  // Make sure this drawable exists
            } else {
                messageText.setBackgroundResource(R.drawable.bg_bot_message)  // Make sure this drawable exists
            }
        }
    }
}
