package com.reisdis.musicrecognizer

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Base64
import java.util.Scanner

fun getSpotifyAccessToken(clientId: String, clientSecret: String): String? {
    // Encode client_id and client_secret in Base64
    val clientCredentials = "$clientId:$clientSecret"
    val base64Credentials = Base64.getEncoder().encodeToString(clientCredentials.toByteArray())

    // Spotify API endpoint for token retrieval
    val tokenUrl = "https://accounts.spotify.com/api/token"

    // Request body parameters
    val requestBody = "grant_type=client_credentials"

    // HTTP headers
    val connection = URL(tokenUrl).openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Authorization", "Basic $base64Credentials")
    connection.doOutput = true

    // Write request body
    val outputStream = connection.outputStream
    outputStream.write(requestBody.toByteArray())
    outputStream.flush()
    outputStream.close()

    // Read response
    val responseCode = connection.responseCode
    if (responseCode == HttpURLConnection.HTTP_OK) {
        val scanner = Scanner(connection.inputStream)
        val responseBody = scanner.useDelimiter("\\A").next()
        scanner.close()
        // Extract access token from response
        return responseBody.substringAfter("access_token\":\"").substringBefore("\"")
    } else {
        // Print error message if request failed
        println("Failed to retrieve access token: $responseCode")
        return null
    }
}


suspend fun getTrackInfo(trackId: String, response: (String?) -> Unit) {
    return withContext(Dispatchers.IO) {
        val accessToken = getSpotifyAccessToken("03cb6271eea04d10b01e777b77713966", "6526be3bca8c4ed083ec1a4f30d4f071")
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/tracks/$trackId")
            .header("Authorization", "Bearer $accessToken")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("Unexpected code $response")
            }

            val responseBody = response.body
            responseBody?.string()?.let {
                response(extractImageUrl(jsonResponse = it))
            }
        }
    }
}
fun extractImageUrl(jsonResponse: String): String {

    println("JSON Respo: $jsonResponse")
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    val jsonAdapter: JsonAdapter<Map<*, *>>? =
        moshi.adapter(Map::class.java)

    val jsonObject = jsonAdapter?.fromJson(jsonResponse)

    val album = jsonObject?.get("album") as? Map<*, *>

    val images = album?.get("images") as? List<*>

    val image = images?.firstOrNull() as? Map<*,*>

    val imageUrl = image?.get("url").toString()

    return imageUrl
}
