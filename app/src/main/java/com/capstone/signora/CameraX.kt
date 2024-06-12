package com.capstone.signora

import ImageClassifierHelper
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File

class CameraX : ComponentActivity() {
    private lateinit var galleryButton: Button
    private lateinit var cameraButton: Button
    private lateinit var scanButton: Button
    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var imageClassifierHelper: ImageClassifierHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camerax)

        galleryButton = findViewById(R.id.galleryButton)
        cameraButton = findViewById(R.id.cameraButton)
        scanButton = findViewById(R.id.scanButton)
        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.resultTextView)

        imageClassifierHelper = ImageClassifierHelper(this)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        galleryButton.setOnClickListener { openGallery() }
        cameraButton.setOnClickListener { openCamera() }
        scanButton.setOnClickListener { scanImage() }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_CAMERA)
    }

    private fun scanImage() {
        // Assuming the image is already set to imageView
        imageView.drawable?.let { drawable ->
            val bitmap = (drawable as BitmapDrawable).bitmap
            Log.d("CameraX", "Starting image classification")
            imageClassifierHelper.classify(bitmap) { result ->
                Log.d("CameraX", "Classification result: $result")
                resultTextView.text = "Result: $result"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {
                    val selectedImageUri: Uri? = data.data
                    selectedImageUri?.let {
                        imageView.setImageURI(it)
                    }
                }
                REQUEST_CODE_CAMERA -> {
                    val photo = data.extras?.get("data") as? Bitmap
                    photo?.let {
                        imageView.setImageBitmap(it)
                    }
                }
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_CODE_GALLERY = 20
        private const val REQUEST_CODE_CAMERA = 30
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
    }
}
