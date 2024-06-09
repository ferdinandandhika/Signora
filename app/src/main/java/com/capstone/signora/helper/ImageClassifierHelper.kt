package com.capstone.signora.helper

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ImageClassifierHelper(context: Context, modelPath: String) {
    private val interpreter: Interpreter

    init {
        val assetManager = context.assets
        val model = loadModelFile(assetManager, modelPath)
        interpreter = Interpreter(model)
    }

    fun classify(bitmap: Bitmap): String {
        // Ensure the bitmap size matches the expected input size of the model
        val inputSize = 64 // Replace with the correct input size of your model
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)

        // Convert bitmap to TensorImage
        val inputImageBuffer = TensorImage.fromBitmap(resizedBitmap)

        // Normalize the input image if required by the model
        val normalizedImage = normalizeImage(inputImageBuffer)

        // Create output buffer with the correct size
        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 26), DataType.FLOAT32) // Adjust the output tensor size

        // Run inference
        interpreter.run(normalizedImage.buffer, outputBuffer.buffer.rewind())

        // Process classification results
        val outputArray = outputBuffer.floatArray

        // Log the output array for debugging
        Log.d("ImageClassifierHelper", "Output Array: ${outputArray.joinToString(", ")}")

        // Define the class labels
        val classLabels = listOf(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"
        )

        // Find the index of the highest probability
        val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1

        // Log the max index for debugging
        Log.d("ImageClassifierHelper", "Max Index: $maxIndex")

        // Return the corresponding class label
        return if (maxIndex != -1) classLabels[maxIndex] else "Unknown"
    }

    private fun normalizeImage(tensorImage: TensorImage): TensorImage {
        val imageBuffer = tensorImage.buffer
        val floatArray = FloatArray(imageBuffer.capacity())
        for (i in floatArray.indices) {
            floatArray[i] = (imageBuffer.get(i).toInt() and 0xFF) / 255.0f
        }

        val shape = intArrayOf(1, tensorImage.height, tensorImage.width, 3) // Assuming the image has 3 color channels (RGB)
        val tensorBuffer = TensorBuffer.createFixedSize(shape, DataType.FLOAT32)
        tensorBuffer.loadArray(floatArray)

        tensorImage.load(tensorBuffer)
        return tensorImage
    }

    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
