import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import android.app.Activity
import org.json.JSONObject
import org.json.JSONException

class ImageClassifierHelper(private val context: Context) {

    private val client = OkHttpClient()

    fun classify(bitmap: Bitmap, callback: (String) -> Unit) {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("image", "image.jpg", RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray))
            .build()

        val request = Request.Builder()
            .url("https://api-model-bhsv3nc4bq-uc.a.run.app/predict")
            .post(requestBody)
            .build()

        Log.d("ImageClassifierHelper", "Sending image to API")

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                (context as Activity).runOnUiThread {
                    Log.e("ImageClassifierHelper", "API call failed: ${e.message}", e)
                    callback("Error: API call failed - ${e.message}")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                (context as Activity).runOnUiThread {
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        Log.d("ImageClassifierHelper", "API response: $responseBody")
                        val result = parseClassFromResponse(responseBody)
                        callback(result ?: "Error: Empty response from server")
                    } else {
                        val errorBody = response.body?.string()
                        Log.e("ImageClassifierHelper", "Failed with HTTP code ${response.code} and message: ${response.message}, error body: $errorBody")
                        callback("Error: Server encountered an issue. Please try again later.")
                    }
                }
            }
        })
    }

    private fun parseClassFromResponse(responseBody: String?): String? {
        return try {
            val jsonObject = JSONObject(responseBody)
            val statusObject = jsonObject.getJSONObject("status")
            val dataObject = statusObject.getJSONObject("data")
            dataObject.getString("class")
        } catch (e: JSONException) {
            Log.e("ImageClassifierHelper", "Failed to parse response", e)
            null
        }
    }
}