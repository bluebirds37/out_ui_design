package com.trailnote.android.core.network

data class ApiEnvelope<T>(
    val success: Boolean,
    val code: String,
    val message: String,
    val data: T,
)

data class PageEnvelope<T>(
    val records: List<T>,
    val total: Int,
    val page: Int,
    val pageSize: Int,
)
