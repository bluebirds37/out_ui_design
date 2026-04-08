package com.trailnote.android.core.config

import com.trailnote.android.BuildConfig

object TrailEnvironment {
    const val accessTokenStorageKey = "trailnote_access_token"

    val apiBaseUrl: String
        get() = BuildConfig.TRAILNOTE_API_BASE_URL

    val useMockData: Boolean
        get() = BuildConfig.TRAILNOTE_USE_MOCK_DATA
}
