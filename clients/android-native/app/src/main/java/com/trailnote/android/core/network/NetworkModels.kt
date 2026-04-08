package com.trailnote.android.core.network

enum class HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
}

data class NetworkRetryPolicy(
    val retries: Int = 1,
    val delayMs: Long = 350,
    val retryMethods: Set<HttpMethod> = setOf(HttpMethod.GET),
    val retryStatuses: Set<Int> = setOf(408, 429, 500, 502, 503, 504),
)

data class RequestOptions(
    val method: HttpMethod = HttpMethod.GET,
    val path: String,
    val query: Map<String, Any?> = emptyMap(),
    val body: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val timeoutMs: Int = 8_000,
    val skipAuth: Boolean = false,
    val retryPolicy: NetworkRetryPolicy? = null,
)

data class NetworkError(
    val code: String,
    val message: String,
    val status: Int,
    val requestId: String? = null,
)

class NetworkException(
    val error: NetworkError,
) : IllegalStateException(error.message)
