package com.trailnote.android.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.trailnote.android.designsystem.theme.Pine700
import com.trailnote.android.designsystem.theme.TrailNoteRadius

@Composable
fun TrailNotePrimaryButton(
    title: String,
    icon: ImageVector? = null,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(TrailNoteRadius.card),
        colors = ButtonDefaults.buttonColors(containerColor = Pine700),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (icon != null) {
                Icon(imageVector = icon, contentDescription = null)
            }
            Text(text = title)
        }
    }
}
