package com.capstone.signora

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import com.capstone.signora.helper.ImageClassifierHelper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory
import java.io.InputStream
import java.nio.channels.FileChannel

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

        // Inisialisasi ImageClassifierHelper
        imageClassifierHelper = ImageClassifierHelper(this, "modelsignora.tflite")

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        // Set up the listener for the capture button
        captureButton.setOnClickListener { takePhoto() }

        // Set up the listener for the flash button
        flashButton.setOnClickListener { toggleFlash() }

        // Set up the listener for the switch camera button
        switchCameraButton.setOnClickListener { switchCamera() }

        // Set up the listener for the gallery button
        galleryButton.setOnClickListener { openGallery() }

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setFlashMode(ImageCapture.FLASH_MODE_OFF) // Set initial flash mode
                .build()

            // Image Analysis
            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, YourImageAnalyzer(imageClassifierHelper, resultTextView))
                }

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture, imageAnalyzer
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

        // Create time-stamped output file to hold the image
        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg"
        )

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has been taken
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
                }
            })
    }

    private fun toggleFlash() {
        imageCapture?.let {
            val currentFlashMode = it.flashMode
            it.flashMode = if (currentFlashMode == ImageCapture.FLASH_MODE_ON) {
                ImageCapture.FLASH_MODE_OFF
            } else {
                ImageCapture.FLASH_MODE_ON
            }
            // Optionally, update the flash button icon to reflect the current flash mode
            flashButton.setImageResource(
                if (it.flashMode == ImageCapture.FLASH_MODE_ON) {
                    R.drawable.baseline_flash_on_24 // Replace with your flash on icon
                } else {
                    R.drawable.baseline_flash_off_24 // Replace with your flash off icon
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
                // Handle the selected image URI if needed
            } else {
                // Restart the camera if no image was selected
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
        // Unbind all use cases to release the camera
        ProcessCameraProvider.getInstance(this).get().unbindAll()
    }

    override fun onStop() {
        super.onStop()
        // Unbind all use cases to release the camera
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

    private class YourImageAnalyzer(
        private val imageClassifierHelper: ImageClassifierHelper,
        private val resultTextView: TextView
    ) : ImageAnalysis.Analyzer {
        override fun analyze(imageProxy: ImageProxy) {
            val buffer: ByteBuffer = imageProxy.planes[0].buffer
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes)

            // Convert ByteBuffer to Bitmap
            val yuvImage = YuvImage(bytes, ImageFormat.NV21, imageProxy.width, imageProxy.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, imageProxy.width, imageProxy.height), 100, out)
            val imageBytes = out.toByteArray()
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

            // Convert bitmap to RGB format and resize to the expected input size of the model
            val rgbBitmap = Bitmap.createScaledBitmap(bitmap.copy(Bitmap.Config.ARGB_8888, true), 64, 64, true)

            // Classify the image
            val result = imageClassifierHelper.classify(rgbBitmap)

            // Update the UI on the main thread
            resultTextView.post {
                resultTextView.text = "Result: $result"
            }

            imageProxy.close()
        }
    }
}
