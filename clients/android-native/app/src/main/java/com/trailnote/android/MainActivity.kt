package com.trailnote.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.trailnote.android.designsystem.theme.TrailNoteTheme
import com.trailnote.android.session.TrailSessionStore
import com.trailnote.android.ui.TrailNoteAndroidApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TrailSessionStore.initialize(applicationContext)
        setContent {
            TrailNoteTheme {
                TrailNoteAndroidApp()
            }
        }
    }
}
