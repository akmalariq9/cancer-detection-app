package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.yalantis.ucrop.UCrop
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var initImageUri: Uri? = null
    private var croppedImageUri: Uri? = null
    private lateinit var bottomNavigationBar : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomNavigationBar = findViewById(R.id.menuBar)
        bottomNavigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.read_news -> {
                    val intent = Intent(this,NewsActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.history_page -> {
                    val intent = Intent(this,HistoryActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        binding.galleryButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, null)
            launcherIntentGallery.launch(chooser)
        }

        binding.analyzeButton.setOnClickListener {
            initImageUri?.let {
                val intent = Intent(this, ResultActivity::class.java)
                croppedImageUri?.let { uri ->
                    intent.putExtra(ResultActivity.IMAGE_URI, uri.toString())
                } ?: showToast(getString(R.string.image_classifier_failed))

                val resultIntent = Intent(this, ResultActivity::class.java)
                croppedImageUri?.let { uri ->
                    resultIntent.putExtra(ResultActivity.IMAGE_URI, uri.toString())
                    startActivity(resultIntent)
                } ?: showToast(getString(R.string.image_classifier_failed))
            } ?: run {
                showToast(getString(R.string.image_classifier_failed))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data
            selectedImg?.let { uri ->
                initImageUri = uri
                showImage()
                uCropUtils(uri)
            } ?: showToast("There is a problem to get URI form the image")
        }
    }

    private fun uCropUtils(sourceUri: Uri) {
        val maxWidth = 3000
        val maxHeight = 3000
        val fileName = "${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, fileName))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(2f, 2f)
            .withMaxResultSize(maxWidth, maxHeight)
            .start(this)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            resultUri?.let {
                showCroppedImage(resultUri)
            } ?: showToast("Error to crop the image, please try again!")
        } else if (resultCode == UCrop.RESULT_ERROR) {
            showToast("Crop error: ${UCrop.getError(data!!)?.message}")
        }
    }

    private fun showImage() {
        initImageUri?.let { uri ->
            binding.previewImageView.setImageURI(uri)
        } ?: showToast(getString(R.string.image_classifier_failed))
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showCroppedImage(uri: Uri) {
        binding.previewImageView.setImageURI(uri)
        croppedImageUri = uri
    }
}
