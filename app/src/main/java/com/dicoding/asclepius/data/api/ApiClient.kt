package com.dicoding.asclepius.data.api

import com.dicoding.asclepius.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val newsApiService: NewsApiService = retrofit.create(NewsApiService::class.java)
}
