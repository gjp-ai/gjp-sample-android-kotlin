package com.ganjianping.ak.sample.features.httpurlconnection

import android.util.Log
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.SSLHandshakeException

enum class HttpMethod(val supportsPayload: Boolean) {
    GET(false),
    POST(true),
    PUT(true),
    DELETE(false)
}

data class HttpResponse(
    val statusCode: Int,
    val body: String,
    val headers: Map<String, String>
)

class HttpURLConnectionRepository {
    private val tag = "HttpURLConnection"

    suspend fun execute(method: HttpMethod, urlText: String, payload: String): HttpResponse = withContext(Dispatchers.IO) {
        Log.d(tag, "Starting ${method.name} request: ${urlText.trim()}")
        val connection = (URL(urlText.trim()).openConnection() as HttpURLConnection).apply {
            requestMethod = method.name
            connectTimeout = 15_000
            readTimeout = 15_000
            doInput = true
            setRequestProperty("Accept", "application/json")
            if (method.supportsPayload) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
        }

        try {
            if (method.supportsPayload && payload.isNotBlank()) {
                connection.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(payload) }
            }

            val statusCode = connection.responseCode
            val stream = if (statusCode in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            Log.d(tag, "Received HTTP $statusCode response (${body.length} characters)")
            HttpResponse(
                statusCode = statusCode,
                body = prettyJson(body),
                headers = connection.headerFields
                    .filterKeys { it != null }
                    .mapKeys { it.key.orEmpty() }
                    .mapValues { it.value.joinToString() }
            )
        } catch (exception: SSLHandshakeException) {
            Log.e(tag, "TLS certificate validation failed for ${urlText.trim()}", exception)
            throw IOException(
                "TLS certificate validation failed. The server must provide a trusted certificate chain.",
                exception
            )
        } catch (exception: Exception) {
            Log.e(tag, "${method.name} request failed: ${exception.message}", exception)
            throw exception
        } finally {
            connection.disconnect()
            Log.d(tag, "Connection closed: ${method.name} ${urlText.trim()}")
        }
    }

    private fun prettyJson(body: String): String {
        val trimmed = body.trim()
        return try {
            when {
                trimmed.startsWith("{") -> JSONObject(trimmed).toString(2)
                trimmed.startsWith("[") -> JSONArray(trimmed).toString(2)
                else -> body
            }
        } catch (_: Exception) {
            body
        }
    }
}
