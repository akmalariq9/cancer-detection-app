package com.dicoding.asclepius.data.api

import com.dicoding.asclepius.BuildConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsRepository {
    fun getHealthNews(
        onSuccess: (List<NewsItem>) -> Unit,
        onFailure: (String) -> Unit
    ){
        ApiClient.newsApiService.healthArticle("kanker", "id", BuildConfig.API_KEY)
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (response.isSuccessful) {
                        val articlesCheck = response.body()?.articles ?: emptyList()
                        val newsList = articlesCheck.mapNotNull { article ->
                            if (article.title.isNotEmpty() && !article.urlToImage.isNullOrEmpty() && !article.url.isNullOrEmpty()) {
                                NewsItem(article.title, article.urlToImage, article.url)
                            } else {
                                null
                            }
                        }
                        onSuccess(newsList)
                    } else {
                        onFailure("There is something wrong")
                    }
                }
                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    onFailure("Unknown error")
                }
            })

        }
}
