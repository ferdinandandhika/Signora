package com.capstone.signora

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FotoProfilActivity : AppCompatActivity() {

    private lateinit var previewImageView: CircleImageView
    private lateinit var currentPhotoPath: String
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath)
            val uri = FileProvider.getUriForFile(this, "com.capstone.signora.fileprovider", file)
            previewImageView.setImageURI(uri)
            currentPhotoPath = uri.toString() // Update currentPhotoPath to URI string by Muhammad Adi Kurnianto
        }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            if (uri != null) {
                previewImageView.setImageURI(uri)
                currentPhotoPath = uri.toString()
            }
        }
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            // Handle permission denial by Muhammad Adi Kurnianto
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_foto_profil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            WindowInsetsCompat.CONSUMED
        }

        auth = FirebaseAuth.getInstance()
        previewImageView = findViewById(R.id.previewImageView)
        progressBar = findViewById(R.id.progressBar)

        // Handle back button click by Muhammad Adi Kurnianto
        val backButton = findViewById<ImageButton>(R.id.back_button)
        backButton.setOnClickListener {
            finish()
        }

        // Handle camera button click by Muhammad Adi Kurnianto
        val cameraButton = findViewById<Button>(R.id.cameraButton)
        cameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        // Handle gallery button click by Muhammad Adi Kurnianto
        val galleryButton = findViewById<Button>(R.id.galleryButton)
        galleryButton.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }

        // Handle upload button click by Muhammad Adi Kurnianto
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        uploadButton.setOnClickListener {
            if (::currentPhotoPath.isInitialized && currentPhotoPath.isNotEmpty()) {
                val uri = Uri.parse(currentPhotoPath)
                uploadImageToFirebase(uri)
            } else {
                Log.e("FotoProfilActivity", "currentPhotoPath is not initialized or empty")
                Toast.makeText(this, "No photo selected to upload", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            Log.d("FotoProfilActivity", "Attempting to create image file")
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                Log.e("FotoProfilActivity", "Error creating image file", ex)
                null
            }
            if (photoFile != null) {
                Log.d("FotoProfilActivity", "Photo file created: ${photoFile.absolutePath}")
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.capstone.signora.fileprovider",
                    photoFile
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                currentPhotoPath = photoFile.absolutePath // Update currentPhotoPath with absolute path
                cameraLauncher.launch(cameraIntent)
            } else {
                Log.e("FotoProfilActivity", "Photo file is null")
            }
        } else {
            Log.e("FotoProfilActivity", "No camera app available")
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
            Log.d("FotoProfilActivity", "Image file created: $currentPhotoPath")
        }
    }

    private fun uploadImageToFirebase(uri: Uri) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val storageRef = FirebaseStorage.getInstance().reference.child("profile_images/${user.uid}/${uri.lastPathSegment}")

            // Show progress bar by Muhammad Adi Kurnianto
            progressBar.visibility = View.VISIBLE

            val inputStream = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val uploadTask = storageRef.putStream(inputStream)

                uploadTask.addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        saveImageUrlToDatabase(downloadUri.toString())
                        // Hide progress bar by Muhammad Adi Kurnianto
                        progressBar.visibility = View.GONE
                    }
                }.addOnFailureListener { exception ->
                    // Handle unsuccessful uploads by Muhammad Adi Kurnianto
                    Log.e("FotoProfilActivity", "Upload failed", exception)
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                    // Hide progress bar by Muhammad Adi Kurnianto
                    progressBar.visibility = View.GONE
                }
            } else {
                Log.e("FotoProfilActivity", "InputStream is null for URI: $uri")
                Toast.makeText(this, "Failed to get InputStream for the selected file", Toast.LENGTH_SHORT).show()
                // Hide progress bar by Muhammad Adi Kurnianto
                progressBar.visibility = View.GONE
            }
        } else {
            Log.e("FotoProfilActivity", "User is not authenticated")
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
            // Hide progress bar by Muhammad Adi Kurnianto
            progressBar.visibility = View.GONE
        }
    }

    private fun saveImageUrlToDatabase(imageUrl: String) {
        val user = auth.currentUser
        if (user != null) {
            val database = FirebaseDatabase.getInstance("https://signora-e8d6b-default-rtdb.asia-southeast1.firebasedatabase.app")
            val databaseRef = database.getReference("users").child(user.uid).child("profileImageUrl")
            databaseRef.setValue(imageUrl).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FotoProfilActivity", "Profile image URL saved successfully: $imageUrl")
                    Toast.makeText(this, "Profile image updated successfully", Toast.LENGTH_SHORT).show()
                    // Notify MainActivity and UserActivity to refresh the profile image by Muhammad Adi Kurnianto
                    val intent = Intent("com.capstone.signora.PROFILE_IMAGE_UPDATED")
                    intent.putExtra("imageUrl", imageUrl)
                    sendBroadcast(intent)
                } else {
                    Log.e("FotoProfilActivity", "Failed to save image URL", task.exception)
                    Toast.makeText(this, "Failed to update profile image", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Log.e("FotoProfilActivity", "User is not authenticated")
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
