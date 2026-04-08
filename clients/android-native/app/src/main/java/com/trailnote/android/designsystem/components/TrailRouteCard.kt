package com.trailnote.android.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.trailnote.android.designsystem.theme.Mist100
import com.trailnote.android.designsystem.theme.Pine500
import com.trailnote.android.designsystem.theme.Pine900
import com.trailnote.android.designsystem.theme.TrailNoteRadius
import com.trailnote.android.designsystem.theme.TrailNoteSpacing

@Composable
fun TrailRouteCard(
    title: String,
    meta: String,
    tags: List<String>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(TrailNoteRadius.card),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.medium),
            modifier = Modifier.padding(TrailNoteSpacing.medium),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        brush = Brush.linearGradient(listOf(Pine900, Pine500)),
                        shape = RoundedCornerShape(TrailNoteRadius.card),
                    ),
            )

            Column(verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.small)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Text(text = meta, style = MaterialTheme.typography.bodyLarge)

                Row(horizontalArrangement = Arrangement.spacedBy(TrailNoteSpacing.xSmall)) {
                    tags.forEach { tag ->
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier
                                .background(
                                    color = Mist100,
                                    shape = RoundedCornerShape(999.dp),
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }
    }
}
