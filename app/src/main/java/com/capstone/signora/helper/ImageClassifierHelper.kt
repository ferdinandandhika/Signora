import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class ImageClassifierHelper(context: Context, modelPath: String) {

    private var interpreter: Interpreter

    init {
        val assetManager = context.assets
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = fileDescriptor.createInputStream()
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        val model = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        interpreter = Interpreter(model)
    }

    fun classify(bitmap: Bitmap): String {
        // Ubah ukuran bitmap ke ukuran yang diharapkan oleh model (misalnya 128x128)
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, true)

        val inputImage = TensorImage.fromBitmap(resizedBitmap)
        val inputBuffer = inputImage.buffer

        val outputBuffer = TensorBuffer.createFixedSize(intArrayOf(1, 26), DataType.FLOAT32)
        interpreter.run(inputBuffer, outputBuffer.buffer.rewind())

        val outputArray = outputBuffer.floatArray
        val maxIndex = outputArray.indices.maxByOrNull { outputArray[it] } ?: -1

        return "Alphabet: ${'A' + maxIndex}"
    }
}