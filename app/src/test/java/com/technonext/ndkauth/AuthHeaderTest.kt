package com.technonext.ndkauth

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthHeaderTest {
    @Test
    fun `adds AuthKey header`() {
        val server = MockWebServer()
        server.enqueue(MockResponse().setBody("{\"ok\":true}").setResponseCode(200))
        server.start()

        try {
            val baseUrl = server.url("/").toString()
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthKeyInterceptor())
                .build()
            val req = Request.Builder().url(baseUrl).build()
            val resp = client.newCall(req).execute()
            assertTrue(resp.request.header("AuthKey")?.isNotBlank() == true)
        } finally {
            server.shutdown()
        }
    }
}
