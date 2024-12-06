package com.example.capstoneprojectmd.ui.chatbot

import android.os.Bundle
import android.widget.Toast
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstoneprojectmd.databinding.FragmentChatBinding
import com.example.capstoneprojectmd.model.Message  // Pastikan untuk mengimpor Message class

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupChat()
    }

    private fun setupChat() {
        val chatMessages = mutableListOf<Message>()
        val chatAdapter = ChatAdapter(chatMessages)

        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = chatAdapter
        }

        binding.sendButton.setOnClickListener {
            val userMessage = binding.messageInput.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                val userMessageObj = Message(content = userMessage, sender = "user")
                chatMessages.add(userMessageObj)
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                binding.chatRecyclerView.scrollToPosition(chatMessages.size - 1)

                ApiClient.classifyText(
                    inputText = userMessage,
                    onSuccess = { result ->
                        requireActivity().runOnUiThread {
                            val botMessageObj = Message(content = result, sender = "bot")
                            chatMessages.add(botMessageObj)
                            chatAdapter.notifyItemInserted(chatMessages.size - 1)
                            binding.chatRecyclerView.scrollToPosition(chatMessages.size - 1)
                        }
                    },
                    onError = { error ->
                        requireActivity().runOnUiThread {
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            } else {
                Toast.makeText(requireContext(), "Pesan tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
