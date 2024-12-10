package com.example.capstoneprojectmd.ui.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstoneprojectmd.databinding.FragmentChatBinding
import kotlinx.coroutines.flow.collectLatest

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext()).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenStarted {
            chatViewModel.chatState.collectLatest { state ->
                chatAdapter.submitList(state.chatList)
                if (state.chatList.isNotEmpty()) {
                    binding.recyclerView.scrollToPosition(0)
                }
            }
        }
    }

    private fun setupListeners() {
        // Handle send button click
        binding.sendButton.setOnClickListener {
            val userMessage = binding.etMessage.text.toString()
            if (userMessage.isNotEmpty()) {
                chatViewModel.onEvent(ChatUiEvent.SendPrompt(userMessage, null))
                binding.etMessage.text.clear()
            } else {
                Toast.makeText(requireContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        binding.etMessage.setOnEditorActionListener { _, _, _ ->
            val userMessage = binding.etMessage.text.toString()
            if (userMessage.isNotEmpty()) {
                chatViewModel.onEvent(ChatUiEvent.SendPrompt(userMessage, null))
                binding.etMessage.text.clear()
            }
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
