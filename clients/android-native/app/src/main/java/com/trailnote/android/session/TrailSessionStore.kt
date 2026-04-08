package com.trailnote.android.session

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.trailnote.android.core.config.TrailEnvironment

object TrailSessionStore {
    var currentToken: String? by mutableStateOf(null)
        private set

    fun initialize(context: Context) {
        val preferences = context.getSharedPreferences("trailnote_session", Context.MODE_PRIVATE)
        currentToken = preferences.getString(TrailEnvironment.accessTokenStorageKey, null)
    }

    fun save(context: Context, accessToken: String) {
        val preferences = context.getSharedPreferences("trailnote_session", Context.MODE_PRIVATE)
        preferences.edit().putString(TrailEnvironment.accessTokenStorageKey, accessToken).apply()
        currentToken = accessToken
    }

    fun clear(context: Context) {
        val preferences = context.getSharedPreferences("trailnote_session", Context.MODE_PRIVATE)
        preferences.edit().remove(TrailEnvironment.accessTokenStorageKey).apply()
        currentToken = null
    }

    fun clearUnauthorizedSession() {
        currentToken = null
    }
}
