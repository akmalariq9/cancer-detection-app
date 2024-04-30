package com.dicoding.asclepius.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("everything")
    fun healthArticle(
        @Query("q") query: String,
        @Query("language") language: String,
        @Query("apiKey") apiKey: String
    ): Call<NewsResponse>
}
