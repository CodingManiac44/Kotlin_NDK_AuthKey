package com.technonext.ndkauth

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.time.Instant

class AuthKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        // Payload could be path + timestamp to keep it dynamic
        val payload = original.url.encodedPath + "|" + Instant.now().epochSecond
        val key = NativeCrypto.generateAuthKey(payload)
        val newReq = original.newBuilder()
            .header("AuthKey", key)
            .build()
        return chain.proceed(newReq)
    }
}

object ApiClient {
    fun create(baseUrl: String): PlaceholderApi {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthKeyInterceptor())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PlaceholderApi::class.java)
    }
}

interface PlaceholderApi {
    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Map<String, Any?>
}
