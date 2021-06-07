package com.nrojiani.bartender.data.test.utils

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import java.util.concurrent.TimeUnit
import kotlin.time.Duration

/**
 * Adapted from:
 * [GitHub: Karumi/KataTODOApiClientKotlin - MockWebServerTest.kt](https://github.com/Karumi/KataTODOApiClientKotlin/blob/master/src/test/kotlin/com/karumi/todoapiclient/MockWebServerTest.kt)
 */
open class MockWebServerTest {

    private lateinit var server: MockWebServer
    private val requests: MutableList<RecordedRequest> = ArrayList()

    protected val baseEndpoint: String
        get() = server.url("/").toString()

    @Before
    open fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
        requests.clear()
    }

    fun enqueueMockResponse(code: Int = 200, fileName: String) {
        val json: String = readMockApiResponseJsonFile(fileName)

        val mockResponse = MockResponse().apply {
            setBody(json)
            setResponseCode(code)
        }

        server.enqueue(mockResponse)
    }

    fun takeRequest(timeout: Duration = DEFAULT_REQUEST_TIMEOUT): RecordedRequest =
        server.takeRequest(timeout.inWholeMilliseconds, TimeUnit.MILLISECONDS)

    fun getAllRecordedRequests(timeout: Duration = DEFAULT_REQUEST_TIMEOUT): List<RecordedRequest> =
        (0 until server.requestCount).fold(emptyList()) { acc, _ ->
            acc + takeRequest(timeout)
        }

    /**
     * Reads a JSON file
     */
    private fun readMockApiResponseJsonFile(filename: String): String =
        readMockJson(filename = filename)

    companion object {
        private val DEFAULT_REQUEST_TIMEOUT: Duration = Duration.seconds(5)
    }
}