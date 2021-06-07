package com.nrojiani.bartender.data.test.utils

import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.RecordedRequest

internal fun RecordedRequest.shouldHaveQueryParam(key: String, expectedValue: String) {
    requestUrl?.queryParameterValues(key)
        .shouldNotBeNull()
        .shouldHaveSize(1)
        .shouldContain(expectedValue)
}

internal fun RecordedRequest.shouldContainHeaders(headers: Map<String, String>) {
    headers.forEach { (name, expectedValue) ->
        this.getHeader(name).shouldBe(expectedValue)
    }
}

enum class HttpRequestMethod {
    GET, HEAD, PUT, POST, PATCH, DELETE, CONNECT, OPTIONS, TRACE
}

internal fun RecordedRequest.shouldBeHttpMethod(httpMethod: HttpRequestMethod) =
    method.shouldBe(httpMethod.name)

internal fun RecordedRequest.shouldHavePath(pathRequestSentTo: String) =
    path.shouldBeIn(setOf(pathRequestSentTo, "/$pathRequestSentTo"))
