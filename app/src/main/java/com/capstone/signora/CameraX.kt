package com.capstone.signora

import ImageClassifierHelper
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraX : ComponentActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var overlay: ImageView
    private lateinit var captureButton: ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var flashButton: ImageButton
    private lateinit var switchCameraButton: ImageButton
    private lateinit var instruction: TextView
    private lateinit var logo: ImageView
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private lateinit var resultTextView: TextView

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camerax)

        previewView = findViewById(R.id.previewView)
        overlay = findViewById(R.id.overlay)
        captureButton = findViewById(R.id.captureButton)
        galleryButton = findViewById(R.id.galleryButton)
        flashButton = findViewById(R.id.flashButton)
        switchCameraButton = findViewById(R.id.switchCameraButton)
        instruction = findViewById(R.id.instruction)
        resultTextView = findViewById(R.id.resultTextView)

        imageClassifierHelper = ImageClassifierHelper(this, "modelsignora.tflite")

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        captureButton.setOnClickListener { takePhoto() }
        flashButton.setOnClickListener { toggleFlash() }
        switchCameraButton.setOnClickListener { switchCamera() }
        galleryButton.setOnClickListener { openGallery() }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                .build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun switchCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }
        startCamera()
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    Log.d(TAG, "Photo capture succeeded: $savedUri")
                    overlay.setImageURI(savedUri)
                    overlay.visibility = View.VISIBLE

                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    classifyImage(bitmap)
                }
            })
    }

    private fun classifyImage(bitmap: Bitmap) {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, true)
        val result = imageClassifierHelper.classify(resizedBitmap)

        resultTextView.post {
            resultTextView.text = "Result: $result"
        }
    }

    private fun toggleFlash() {
        imageCapture?.let {
            val currentFlashMode = it.flashMode
            it.flashMode = if (currentFlashMode == ImageCapture.FLASH_MODE_ON) {
                ImageCapture.FLASH_MODE_OFF
            } else {
                ImageCapture.FLASH_MODE_ON
            }
            flashButton.setImageResource(
                if (it.flashMode == ImageCapture.FLASH_MODE_ON) {
                    R.drawable.baseline_flash_on_24
                } else {
                    R.drawable.baseline_flash_off_24
                }
            )
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (resultCode == RESULT_OK && data != null) {
                val selectedImageUri: Uri? = data.data
                selectedImageUri?.let {
                    val inputStream = contentResolver.openInputStream(it)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    classifyImage(bitmap)
                }
            } else {
                startCamera()
            }
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                finish()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        ProcessCameraProvider.getInstance(this).get().unbindAll()
    }

    override fun onStop() {
        super.onStop()
        ProcessCameraProvider.getInstance(this).get().unbindAll()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraX"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_CODE_GALLERY = 20
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
