package com.trailnote.android.core.network

import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class TrailNoteHttpClient(
    private val baseUrl: String,
    private val accessTokenProvider: () -> String? = { null },
    private val defaultRetryPolicy: NetworkRetryPolicy = NetworkRetryPolicy(),
) {
    fun buildUrl(path: String, query: Map<String, Any?> = emptyMap()): String {
        val normalizedBase = baseUrl.trimEnd('/')
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        val queryString = query
            .flatMap { (key, value) ->
                when (value) {
                    null -> emptyList()
                    is Iterable<*> -> value.filterNotNull().map { item -> key to item.toString() }
                    is Array<*> -> value.filterNotNull().map { item -> key to item.toString() }
                    else -> listOf(key to value.toString())
                }
            }
            .filterNot { (_, value) -> value.isBlank() }
            .joinToString("&") { (key, value) -> "${key.urlEncode()}=${value.urlEncode()}" }

        return buildString {
            append(normalizedBase)
            append(normalizedPath)
            if (queryString.isNotBlank()) {
                append("?")
                append(queryString)
            }
        }
    }

    fun buildConnection(options: RequestOptions): HttpURLConnection {
        val connection = URL(buildUrl(options.path, options.query)).openConnection() as HttpURLConnection
        connection.requestMethod = options.method.name
        connection.connectTimeout = options.timeoutMs
        connection.readTimeout = options.timeoutMs
        connection.setRequestProperty("Accept", "application/json")

        if (!options.skipAuth) {
            accessTokenProvider().orEmpty().takeIf { it.isNotBlank() }?.let { token ->
                connection.setRequestProperty("Authorization", "Bearer $token")
            }
        }

        options.headers.forEach { (key, value) ->
            connection.setRequestProperty(key, value)
        }

        if (options.body != null) {
            connection.doOutput = true
            connection.setRequestProperty("Content-Type", "application/json")
        }

        return connection
    }

    fun resolveRetryPolicy(options: RequestOptions): NetworkRetryPolicy {
        return options.retryPolicy ?: defaultRetryPolicy
    }
}

private fun String.urlEncode(): String = URLEncoder.encode(this, Charsets.UTF_8.name())
