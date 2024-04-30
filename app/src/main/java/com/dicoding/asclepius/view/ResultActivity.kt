package com.dicoding.asclepius.view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.data.database.HistoryDatabase
import com.dicoding.asclepius.data.database.History
import com.dicoding.asclepius.helper.ImageClassifierHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.io.FileOutputStream

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    companion object {
        const val IMAGE_URI = "image_url"
        const val RESULT_TEXT = "result_text"
        const val REQUEST_HISTORY_UPDATE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriString = intent.getStringExtra(IMAGE_URI)
        if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            showImage(imageUri)

            val imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(errorMsg: String) {
                        showToast("Error")
                    }

                    override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                        results?.let { showResults(it) }
                    }
                }
            )
            imageClassifierHelper.classifyImage(imageUri)
        } else {
            finish()
        }

        binding.saveButton.setOnClickListener {
            val result = binding.resultText.text.toString()

            if (intent.getStringExtra(IMAGE_URI) == null) {
                showToast("No image URI provided")
                finish()
            } else {
                saveHistory(Uri.parse(imageUriString), result)
                showToast("Data saved")
            }
        }
    }

    private fun showImage(uri: Uri) {
        binding.resultImage.setImageURI(uri)
    }

    @SuppressLint("SetTextI18n")
    private fun showResults(results: List<Classifications>) {
        val topResult = results[0]
        val label = topResult.categories[0].label
        val score = topResult.categories[0].score

        fun Float.formatToString(): String {
            return String.format("%.2f%%", this * 100)
        }
        binding.resultText.text = "$label ${score.formatToString()}"
    }

    private fun moveToHistory(imageUri: Uri, result: String) {
        val intent = Intent(this, HistoryActivity::class.java)
        intent.putExtra(RESULT_TEXT, result)
        intent.putExtra(IMAGE_URI, imageUri.toString())
        setResult(RESULT_OK, intent)
        startActivity(intent)
        finish()
    }

    private fun saveHistory(imageUri: Uri, result: String) {
        if (result.isNotEmpty()) {
            val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
            val destinationUri = Uri.fromFile(File(cacheDir, fileName))
            contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(File(cacheDir, fileName)).use { output ->
                    input.copyTo(output)
                }
            }
            val history = History(imageURL = destinationUri.toString(), result = result)
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.IO) {
                    val database = HistoryDatabase.getDatabase(applicationContext)
                    try {
                        database.HistoryDao().insertData(history)
                        moveToHistory(destinationUri, result)
                    } catch (e: Exception) {
                        showToast("Failed to save data!")
                    }
                }
            }
        } else {
            showToast("Failed to save data!")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
