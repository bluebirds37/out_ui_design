package com.trailnote.android.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

enum class TrailNoteTab(
    val label: String,
    val icon: ImageVector,
) {
    DISCOVER("发现", Icons.Filled.Home),
    SEARCH("搜索", Icons.Filled.Search),
    RECORD("记录", Icons.Filled.Explore),
    MINE("我的", Icons.Filled.Person),
}
