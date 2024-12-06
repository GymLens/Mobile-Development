package com.example.capstoneprojectmd.ui.beranda

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstoneprojectmd.data.response.DataItem
import com.example.capstoneprojectmd.databinding.ListItemArticleBinding

class ArticleAdapter(private var articles: List<DataItem>) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ListItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = articles.size

    fun updateArticles(newArticles: List<DataItem>) {
        articles = newArticles
        notifyDataSetChanged()
    }

    inner class ArticleViewHolder(private val binding: ListItemArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: DataItem) {
            binding.titleTextView.text = article.title
            Glide.with(binding.imageView.context)
                .load(article.image)
                .placeholder(android.R.color.darker_gray)
                .into(binding.imageView)

            binding.root.setOnClickListener {
                // Handle click event
            }
        }
    }
}
