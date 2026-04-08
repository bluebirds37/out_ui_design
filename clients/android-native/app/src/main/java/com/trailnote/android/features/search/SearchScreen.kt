package com.trailnote.android.features.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.trailnote.android.data.service.TrailServiceRegistry
import com.trailnote.android.data.service.TrailRouteSummary
import com.trailnote.android.designsystem.components.TrailRouteCard
import com.trailnote.android.designsystem.theme.Mist100
import com.trailnote.android.designsystem.theme.TrailNoteSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SearchScreen(
    onOpenRoute: (Long) -> Unit,
) {
    val routeService = TrailServiceRegistry.routeService
    val screenState by produceState<SearchScreenState>(
        initialValue = SearchScreenState.Loading,
        key1 = routeService,
    ) {
        value = try {
            val result = withContext(Dispatchers.IO) { routeService.search(keyword = "山脊") }
            SearchScreenState.Success(
                keyword = result.keyword,
                routes = result.routes,
            )
        } catch (error: Throwable) {
            SearchScreenState.Error(error.message ?: "搜索结果加载失败")
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.large),
        modifier = Modifier
            .fillMaxSize()
            .background(Mist100)
            .padding(TrailNoteSpacing.large),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.small)) {
                Text(text = "搜索路线", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = "按目的地、作者和点位类型快速定位适合这次出发的路线。",
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }

        when (val state = screenState) {
            SearchScreenState.Loading -> {
                item {
                    SearchStatusCard(
                        title = "搜索结果同步中",
                        body = "正在根据关键词拉取真实路线数据。",
                    )
                }
            }

            is SearchScreenState.Error -> {
                item {
                    SearchStatusCard(
                        title = "搜索失败",
                        body = state.message,
                    )
                }
            }

            is SearchScreenState.Success -> {
                item {
                    Text(
                        text = "当前关键词：${state.keyword}",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }

                items(state.routes) { route ->
                    TrailRouteCard(
                        title = route.title,
                        meta = "${route.authorName} · ${route.distanceKm}km · ${formatDifficulty(route.difficulty)}",
                        tags = route.tags,
                    )
                    SearchActionButton(onClick = { onOpenRoute(route.id) })
                }
            }
        }
    }
}

@Composable
private fun SearchActionButton(onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = onClick,
    ) {
        Text(
            text = "进入互动详情",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(TrailNoteSpacing.medium),
        )
    }
}

private sealed interface SearchScreenState {
    data object Loading : SearchScreenState

    data class Success(
        val keyword: String,
        val routes: List<TrailRouteSummary>,
    ) : SearchScreenState

    data class Error(
        val message: String,
    ) : SearchScreenState
}

private fun formatDifficulty(value: String): String {
    return when (value) {
        "BEGINNER" -> "新手友好"
        "INTERMEDIATE" -> "中阶强度"
        "ADVANCED" -> "高阶挑战"
        else -> value
    }
}

@Composable
private fun SearchStatusCard(
    title: String,
    body: String,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.small),
            modifier = Modifier.padding(TrailNoteSpacing.large),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium)
            Text(text = body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
