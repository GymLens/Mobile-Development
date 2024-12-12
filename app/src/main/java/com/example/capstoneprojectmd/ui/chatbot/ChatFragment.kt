package com.example.capstoneprojectmd.ui.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstoneprojectmd.data.model.Chat
import com.example.capstoneprojectmd.databinding.FragmentChatBinding


class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    private val chatViewModel: ChatViewModel by viewModels()
    private val chatAdapter = ChatAdapter(mutableListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = chatAdapter

        // Mengamati respons chat
        chatViewModel.chatResponse.observe(viewLifecycleOwner) { response ->
            chatAdapter.addChat(Chat(response.message, isFromUser = false))
            binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        }

        // Mengamati status loading
        chatViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Mengamati pesan kesalahan
        chatViewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
        }

        // Mengamati peringatan kata kunci yang hilang
        chatViewModel.keywordAlert.observe(viewLifecycleOwner) { alertMessage ->
            if (alertMessage.isNotEmpty()) {
                // Menampilkan pesan kata kunci yang hilang menggunakan Toast
                Toast.makeText(requireContext(), alertMessage, Toast.LENGTH_SHORT).show()
            }
        }

        // Mengirim permintaan chat ketika tombol kirim ditekan
        binding.btnSend.setOnClickListener {
            val prompt = binding.etChatInput.text.toString().trim()
            if (prompt.isNotBlank()) {
                chatViewModel.sendChatRequest(prompt)
                chatAdapter.addChat(Chat(prompt, isFromUser = true))
                binding.recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                binding.etChatInput.text.clear()
            } else {
                Toast.makeText(requireContext(), "Input cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
