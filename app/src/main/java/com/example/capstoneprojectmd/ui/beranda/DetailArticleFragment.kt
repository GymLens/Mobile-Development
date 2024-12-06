package com.example.capstoneprojectmd.ui.beranda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.capstoneprojectmd.data.response.DataItem
import com.example.capstoneprojectmd.databinding.FragmentDetailArticleBinding

class DetailArticleFragment : Fragment() {

    private var _binding: FragmentDetailArticleBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARTICLE_KEY = "article"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailArticleBinding.inflate(inflater, container, false)

        val article = arguments?.getSerializable(ARTICLE_KEY) as? DataItem
        article?.let {
            binding.titleTextView.text = it.title
            Glide.with(binding.imageView.context)
                .load(it.image)
                .placeholder(android.R.color.darker_gray)
                .into(binding.imageView)

            binding.contentTextView.text = it.url
        }

        binding.fabBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
