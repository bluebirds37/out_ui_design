package com.trailnote.android.features.discover

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
import com.trailnote.android.designsystem.components.TrailNotePrimaryButton
import com.trailnote.android.designsystem.components.TrailRouteShowcaseCard
import com.trailnote.android.designsystem.components.TrailSectionHeading
import com.trailnote.android.designsystem.components.TrailTopHeroCard
import com.trailnote.android.designsystem.theme.Ink950
import com.trailnote.android.designsystem.theme.Lake500
import com.trailnote.android.designsystem.theme.Mist100
import com.trailnote.android.designsystem.theme.Pine500
import com.trailnote.android.designsystem.theme.Pine900
import com.trailnote.android.designsystem.theme.TrailNoteSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun DiscoverScreen(
    onOpenRoute: (Long) -> Unit,
) {
    val routeService = TrailServiceRegistry.routeService
    val screenState by produceState<DiscoverScreenState>(
        initialValue = DiscoverScreenState.Loading,
        key1 = routeService,
    ) {
        value = try {
            val featuredRoutes = withContext(Dispatchers.IO) { routeService.featuredRoutes() }
            val routeList = withContext(Dispatchers.IO) { routeService.routes(page = 1, pageSize = 6).records }
            DiscoverScreenState.Success(
                featuredRoutes = featuredRoutes,
                routeList = routeList,
            )
        } catch (error: Throwable) {
            DiscoverScreenState.Error(error.message ?: "精选路线加载失败")
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
            TrailTopHeroCard(
                eyebrow = "精选路线",
                title = "把山脊线、湖边营地和真实点位装进同一条路线里。",
                body = "发现页优先承接统一的路线卡片、详情入口和远程错误反馈，和 iOS、H5、小程序保持同一信息顺序。",
            )
        }

        when (val state = screenState) {
            DiscoverScreenState.Loading -> {
                item {
                    StatusCard(
                        title = "精选路线同步中",
                        body = "正在拉取发现页真实数据。",
                    )
                }
            }

            is DiscoverScreenState.Error -> {
                item {
                    StatusCard(
                        title = "发现页加载失败",
                        body = state.message,
                    )
                }
            }

            is DiscoverScreenState.Success -> {
                state.featuredRoutes.firstOrNull()?.let { route ->
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.medium)) {
                            TrailRouteShowcaseCard(
                                title = route.title,
                                meta = "${route.distanceKm}km · ${formatDuration(route.durationMinutes)} · 爬升 ${route.ascentM}m",
                                secondaryMeta = "推荐路线 · 关键点位 ${route.waypointCount}",
                                tags = route.tags,
                                accentColors = listOf(Pine900, Pine500),
                            )
                            TrailNotePrimaryButton(title = "查看路线详情", onClick = { onOpenRoute(route.id) })
                        }
                    }
                }

                item {
                    TrailSectionHeading(
                        title = "继续浏览",
                        subtitle = "保持和 H5 / 小程序一致的路线信息结构与按钮层级。",
                    )
                }

                items(state.routeList.withIndex().toList()) { (index, route) ->
                    Column(verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.medium)) {
                        TrailRouteShowcaseCard(
                            title = route.title,
                            meta = "${route.distanceKm}km · 收藏 ${route.favoriteCount} · 点位 ${route.waypointCount}",
                            secondaryMeta = "作者 ${route.authorName}",
                            tags = route.tags,
                            accentColors = if (index % 2 == 0) listOf(Pine900, Pine500) else listOf(Ink950, Lake500),
                        )
                        TrailNotePrimaryButton(title = "进入互动详情", onClick = { onOpenRoute(route.id) })
                    }
                }
            }
        }
    }
}

private sealed interface DiscoverScreenState {
    data object Loading : DiscoverScreenState

    data class Success(
        val featuredRoutes: List<TrailRouteSummary>,
        val routeList: List<TrailRouteSummary>,
    ) : DiscoverScreenState

    data class Error(
        val message: String,
    ) : DiscoverScreenState
}

private fun formatDuration(minutes: Int): String {
    val hour = minutes / 60
    val minute = minutes % 60
    return "${hour}h${minute.toString().padStart(2, '0')}m"
}

@Composable
private fun StatusCard(
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
