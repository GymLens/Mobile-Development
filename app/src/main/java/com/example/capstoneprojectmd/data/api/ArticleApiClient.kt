package com.example.capstoneprojectmd.data.api

import com.google.gson.Gson
import com.example.capstoneprojectmd.data.response.ArticleResponse
import com.example.capstoneprojectmd.data.response.DataItem
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

object ArticleApiClient {
    private const val API_URL = "https://gymlens-user-api-241705916714.asia-southeast2.run.app/api/article"

    private val client = OkHttpClient()
    private val gson = Gson()

    fun fetchArticles(
        onSuccess: (List<DataItem>) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = Request.Builder()
            .url(API_URL)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                onError("Failed to contact the server: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    try {
                        val articleResponse = gson.fromJson(responseBody, ArticleResponse::class.java)
                        val articles = articleResponse.data?.filterNotNull() ?: emptyList()

                        println("Fetched Articles: $articles")
                        onSuccess(articles)
                    } catch (e: Exception) {
                        onError("Error parsing response: ${e.message}")
                    }
                } else {
                    onError("Request failed: ${response.message}")
                }
            }
        })
    }
}
