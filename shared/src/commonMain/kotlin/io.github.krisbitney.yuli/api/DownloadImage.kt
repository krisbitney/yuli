package io.github.krisbitney.yuli.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.core.use
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

suspend fun downloadImage(url: String): Result<ByteArray> = withContext(Dispatchers.IO) {
    HttpClient().use { client ->
        val response: HttpResponse = client.get(url)
        if (response.status.value == 200) {
            Result.success(response.readBytes())
        } else {
            Result.failure(Exception("Failed to download image"))
        }
    }
}