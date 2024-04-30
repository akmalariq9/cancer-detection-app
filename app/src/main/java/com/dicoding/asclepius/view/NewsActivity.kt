package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.NewsAdapter
import com.dicoding.asclepius.data.api.NewsViewModel
import com.dicoding.asclepius.databinding.ActivityNewsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var bottomNavigationBar: BottomNavigationView
    private lateinit var newsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationBar = findViewById(R.id.menuBar)
        newsRecyclerView = findViewById(R.id.rvNewsList)
        bottomNavigationBar.selectedItemId = R.id.read_news
        bottomNavigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.read_news -> {
                    true
                }
                R.id.history_page -> {
                    val intent = Intent(this, HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        newsAdapter = NewsAdapter()
        binding.rvNewsList.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(this@NewsActivity)
        }

        newsViewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        newsViewModel.getHealthNews()
        newsViewModel.newsList.observe(this) { newsList ->
            newsAdapter.submitList(newsList)
        }
    }

    fun readMore(view: View) {
        val url = view.getTag(R.id.tvLink) as? String
        url?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
    }
}
