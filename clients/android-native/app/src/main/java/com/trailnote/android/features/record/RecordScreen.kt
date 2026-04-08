package com.trailnote.android.features.record

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.trailnote.android.designsystem.components.TrailNotePrimaryButton
import com.trailnote.android.designsystem.theme.Mist100
import com.trailnote.android.designsystem.theme.TrailNoteSpacing

@Composable
fun RecordScreen() {
    Column(
        verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.large),
        modifier = Modifier
            .fillMaxSize()
            .background(Mist100)
            .padding(TrailNoteSpacing.large),
    ) {
        Text(text = "记录准备", style = MaterialTheme.typography.labelMedium)
        Text(
            text = "尽量少打断地记录轨迹、点位与现场信息。",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "当前状态：未开始。离线草稿、点位媒体和轨迹状态将在后续阶段继续接入。",
            style = MaterialTheme.typography.bodyLarge,
        )
        TrailNotePrimaryButton(
            title = "开始记录",
            icon = Icons.Default.Place,
            onClick = {},
        )
    }
}
