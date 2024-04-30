package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.HistoryAdapter
import com.dicoding.asclepius.data.database.HistoryDatabase
import com.dicoding.asclepius.data.database.History
import com.dicoding.asclepius.view.ResultActivity.Companion.REQUEST_HISTORY_UPDATE
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity(), HistoryAdapter.OnDeleteClickListener {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyAdapter: HistoryAdapter
    private var historyList: MutableList<History> = mutableListOf()
    private lateinit var tvNotFound: TextView
    private lateinit var bottomNavigationBar: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        bottomNavigationBar = findViewById(R.id.menuBar)
        historyRecyclerView = findViewById(R.id.rvHistory)
        tvNotFound = findViewById(R.id.tvNotFound)

        bottomNavigationBar.selectedItemId = R.id.history_page
        bottomNavigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.read_news -> {
                    val intent = Intent(this, NewsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.history_page -> {
                    true
                }
                else -> false
            }
        }
        historyRecyclerView = findViewById(R.id.rvHistory)
        tvNotFound = findViewById(R.id.tvNotFound)

        historyAdapter = HistoryAdapter(historyList)
        historyAdapter.setOnDeleteClickListener(this)
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        CoroutineScope(Dispatchers.Main).launch {
            loadHistory()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_HISTORY_UPDATE && resultCode == RESULT_OK) {
            CoroutineScope(Dispatchers.Main).launch {
                loadHistory()
            }
        }
    }

    private fun loadHistory() {
        CoroutineScope(Dispatchers.Main).launch {
            val historyData = withContext(Dispatchers.IO) {
                HistoryDatabase.getDatabase(this@HistoryActivity).HistoryDao().getAllData()
            }
            historyList.clear()
            historyList.addAll(historyData)
            historyAdapter.notifyDataSetChanged()
            if (historyList.isEmpty()) {
                tvNotFound.visibility = View.VISIBLE
                historyRecyclerView.visibility = View.GONE
            } else {
                tvNotFound.visibility = View.GONE
                historyRecyclerView.visibility = View.VISIBLE
            }
        }
    }

    override fun onDeleteClick(position: Int) {
        if (historyList[position].result.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    HistoryDatabase.getDatabase(this@HistoryActivity).HistoryDao().deleteData(historyList[position])
                }
                withContext(Dispatchers.Main) {
                    historyList.removeAt(position)
                    historyAdapter.notifyDataSetChanged()
                    if (historyList.isEmpty()) {
                        tvNotFound.visibility = View.VISIBLE
                        historyRecyclerView.visibility = View.GONE
                    } else {
                        tvNotFound.visibility = View.GONE
                        historyRecyclerView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}
