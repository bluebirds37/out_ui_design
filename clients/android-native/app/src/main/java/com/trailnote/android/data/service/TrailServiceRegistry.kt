package com.trailnote.android.data.service

import com.trailnote.android.core.config.TrailEnvironment
import com.trailnote.android.core.network.TrailNoteHttpClient
import com.trailnote.android.session.TrailSessionStore

object TrailServiceRegistry {
    private val httpClient by lazy {
        TrailNoteHttpClient(
            baseUrl = TrailEnvironment.apiBaseUrl,
            accessTokenProvider = { TrailSessionStore.currentToken },
        )
    }

    val authService: AuthService by lazy {
        if (TrailEnvironment.useMockData) {
            InMemoryAuthService()
        } else {
            RemoteAuthService(httpClient)
        }
    }

    val routeService: RouteService by lazy {
        if (TrailEnvironment.useMockData) {
            InMemoryRouteService()
        } else {
            RemoteRouteService(httpClient)
        }
    }

    val profileService: ProfileService by lazy {
        if (TrailEnvironment.useMockData) {
            InMemoryProfileService()
        } else {
            RemoteProfileService(httpClient)
        }
    }

    val authorService: AuthorService by lazy {
        if (TrailEnvironment.useMockData) {
            InMemoryAuthorService()
        } else {
            RemoteAuthorService(httpClient)
        }
    }
}
