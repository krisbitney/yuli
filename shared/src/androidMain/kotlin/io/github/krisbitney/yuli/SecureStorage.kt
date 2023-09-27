package io.github.krisbitney.yuli

import androidx.security.crypto.EncryptedFile
import com.github.instagram4j.instagram4j.IGClient
import com.github.instagram4j.instagram4j.utils.IGUtils
import com.github.instagram4j.instagram4j.utils.SerializableCookieJar
import okhttp3.CookieJar
import okhttp3.OkHttpClient
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

fun IGClient.serializeEncrypted(clientFile: EncryptedFile, cookieFile: EncryptedFile) {
    EncryptedSerializeUtil.serialize(this, clientFile)
    EncryptedSerializeUtil.serialize(httpClient.cookieJar, cookieFile)
}

object IGClientEncryptedDeserializer {
    fun deserialize(clientFile: EncryptedFile, cookieFile: EncryptedFile): IGClient {
        return deserialize(clientFile, cookieFile, IGUtils.defaultHttpClientBuilder())
    }

    fun deserialize(clientFile: EncryptedFile, cookieFile: EncryptedFile, clientBuilder: OkHttpClient.Builder): IGClient {
        val client: IGClient = EncryptedSerializeUtil.deserialize(clientFile, IGClient::class.java)
        val jar: CookieJar = EncryptedSerializeUtil.deserialize(cookieFile, SerializableCookieJar::class.java)

        client.httpClient = clientBuilder
            .cookieJar(jar)
            .build()

        return client
    }
}

object EncryptedSerializeUtil {
    fun serialize(obj: Any, to: EncryptedFile) {
        to.openFileOutput().use { file ->
            ObjectOutputStream(file).use { out ->
                out.writeObject(obj)
            }
        }
    }

    fun <T> deserialize(inputStream: InputStream, clazz: Class<T>): T {
        return ObjectInputStream(inputStream).use { oIn ->
            val obj = oIn.readObject()
            val result: T = clazz.cast(obj)
                ?: throw ClassNotFoundException("Cannot cast deserialized object to class $clazz")
            result
        }
    }

    fun <T> deserialize(from: EncryptedFile, clazz: Class<T>): T {
        return from.openFileInput().use { file ->
            deserialize(file, clazz)
        }
    }
}


