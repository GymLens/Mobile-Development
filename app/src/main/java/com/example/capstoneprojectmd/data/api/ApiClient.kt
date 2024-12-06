import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

object ApiClient {
    private const val API_URL =
        "https://us-central1-aiplatform.googleapis.com/v1/projects/241705916714/locations/us-central1/endpoints/7520317585871601664:generateContent"
    private const val ACCESS_TOKEN = "YOUR_ACCESS_TOKEN"

    private val client = OkHttpClient()

    fun classifyText(
        inputText: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val jsonBody = JSONObject().apply {
                put("instances", JSONArray().put(JSONObject().put("content", inputText)))
                put(
                    "parameters",
                    JSONObject().apply {
                        put("temperature", 0.7)
                        put("maxOutputTokens", 30)
                    }
                )
            }

            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body = RequestBody.create(mediaType, jsonBody.toString())

            val request = Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onError("Gagal menghubungi server: ${e.message}")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        try {
                            val responseBody = response.body?.string()
                            val outputText = JSONObject(responseBody ?: "")
                                .getJSONArray("predictions")
                                .getJSONObject(0)
                                .getString("content")
                            onSuccess(outputText)
                        } catch (e: Exception) {
                            onError("Gagal memproses respons: ${e.message}")
                        }
                    } else {
                        onError("Gagal: ${response.message}")
                    }
                }
            })
        } catch (e: Exception) {
            onError("Terjadi kesalahan: ${e.message}")
        }
    }
}
