package com.anpfuel.data.remote

import com.anpfuel.data.remote.interceptor.RetryInterceptor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class OkHttpClientFactoryTest {

    @Test
    fun createsClientWithExpectedTimeoutsAndRetryDefaults() {
        val client = OkHttpClientFactory.create()

        assertEquals(TimeUnit.SECONDS.toMillis(30), client.connectTimeoutMillis.toLong())
        assertEquals(TimeUnit.SECONDS.toMillis(60), client.readTimeoutMillis.toLong())
        assertEquals(TimeUnit.SECONDS.toMillis(60), client.writeTimeoutMillis.toLong())
        assertTrue(client.interceptors.size >= 3)
    }

    @Test
    fun defaultRetryInterceptorAllowsThreeRetries() {
        assertEquals(3, RetryInterceptor.DEFAULT_MAX_RETRIES)
    }
}
