package com.example.capstoneprojectmd.ui.beranda

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.capstoneprojectmd.data.api.ArticleApiClient
import com.example.capstoneprojectmd.databinding.FragmentBerandaBinding

class BerandaFragment : Fragment() {

    private var _binding: FragmentBerandaBinding? = null
    private val binding get() = _binding!!

    private var userName: String? = null

    private lateinit var articleAdapter: ArticleAdapter

    companion object {
        fun newInstance(userName: String): BerandaFragment {
            val fragment = BerandaFragment()
            val args = Bundle()
            args.putString("USER_NAME", userName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBerandaBinding.inflate(inflater, container, false)

        val fullName = arguments?.getString("USER_NAME") ?: "Guest"
        binding.userNameTextView.text = "Hi, $fullName!"

        setupRecyclerView()

        fetchArticles()

        return binding.root
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(emptyList())
        binding.articlesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = articleAdapter
        }
    }

    private fun fetchArticles() {
        ArticleApiClient.fetchArticles(
            onSuccess = { articles ->
                Log.d("BerandaFragment", "Articles fetched: ${articles.size}")
                requireActivity().runOnUiThread {
                    articleAdapter.updateArticles(articles)
                }
            },
            onError = { error ->
                Log.e("BerandaFragment", "Error fetching articles: $error")
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
