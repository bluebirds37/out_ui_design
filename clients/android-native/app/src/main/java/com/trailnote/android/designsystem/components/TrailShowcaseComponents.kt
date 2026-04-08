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
import com.trailnote.android.designsystem.theme.Stone300
import com.trailnote.android.designsystem.theme.TrailNoteRadius
import com.trailnote.android.designsystem.theme.TrailNoteSpacing

@Composable
fun TrailTopHeroCard(
    eyebrow: String,
    title: String,
    body: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(TrailNoteRadius.sheet),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.small),
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            androidx.compose.ui.graphics.Color(0xFF17352C),
                            androidx.compose.ui.graphics.Color(0xFF3D7A61),
                        ),
                    ),
                )
                .padding(TrailNoteSpacing.large),
        ) {
            Text(text = eyebrow, style = MaterialTheme.typography.labelMedium, color = androidx.compose.ui.graphics.Color(0xFFC59D5F))
            Text(text = title, style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Text(text = body, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.82f))
        }
    }
}

@Composable
fun TrailSectionHeading(
    title: String,
    subtitle: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.xSmall)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun TrailStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(TrailNoteRadius.card),
        colors = CardDefaults.cardColors(containerColor = Mist100),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.xSmall),
            modifier = Modifier.padding(TrailNoteSpacing.medium),
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun TrailRouteShowcaseCard(
    title: String,
    meta: String,
    secondaryMeta: String?,
    tags: List<String>,
    accentColors: List<Color>,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(TrailNoteRadius.sheet),
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
                    .height(180.dp)
                    .background(
                        brush = Brush.linearGradient(accentColors),
                        shape = RoundedCornerShape(TrailNoteRadius.sheet),
                    ),
            )

            Column(verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.small)) {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Text(text = meta, style = MaterialTheme.typography.bodyLarge)
                if (secondaryMeta != null) {
                    Text(
                        text = secondaryMeta,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

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
