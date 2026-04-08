package com.trailnote.android.features.mine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.trailnote.android.data.service.TrailRouteSummary
import com.trailnote.android.data.service.TrailProfile
import com.trailnote.android.data.service.TrailServiceRegistry
import com.trailnote.android.designsystem.components.TrailNotePrimaryButton
import com.trailnote.android.designsystem.components.TrailRouteShowcaseCard
import com.trailnote.android.designsystem.components.TrailSectionHeading
import com.trailnote.android.designsystem.components.TrailStatCard
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
fun MineScreen(
    onOpenRoute: (Long) -> Unit,
    onLogout: () -> Unit,
) {
    val profileService = TrailServiceRegistry.profileService
    val screenState by produceState<MineScreenState>(
        initialValue = MineScreenState.Loading,
        key1 = profileService,
    ) {
        value = try {
            val profile = withContext(Dispatchers.IO) { profileService.myProfile() }
            val favorites = withContext(Dispatchers.IO) { profileService.myFavorites(page = 1, pageSize = 3).records }
            MineScreenState.Success(profile, favorites)
        } catch (error: Throwable) {
            MineScreenState.Error(error.message ?: "个人资料加载失败")
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
                eyebrow = "我的",
                title = "统一管理个人资料、收藏路线和后续创作草稿。",
                body = "个人中心与其他端保持同一层级：资料摘要、关键数据和主动作分开呈现。",
            )
        }

        when (val state = screenState) {
            MineScreenState.Loading -> {
                item {
                    MineStatusCard(
                        title = "个人资料同步中",
                        body = "正在拉取我的资料与收藏概览。",
                    )
                }
            }

            is MineScreenState.Error -> {
                item {
                    MineStatusCard(
                        title = "个人页加载失败",
                        body = state.message,
                    )
                }
            }

            is MineScreenState.Success -> {
                val profile = state.profile
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.medium),
                            modifier = Modifier.padding(TrailNoteSpacing.large),
                        ) {
                            TrailSectionHeading(
                                title = profile.nickname,
                                subtitle = "${profile.city} · ${profile.levelLabel}",
                            )

                            Text(
                                text = profile.bio,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }

                item {
                    StatGrid(
                        stats = listOf(
                            "已发布路线" to profile.publishedRouteCount.toString(),
                            "收藏夹" to profile.favoriteRouteCount.toString(),
                            "资料状态" to "已同步",
                            "当前模式" to "远程联调",
                        ),
                    )
                }

                item {
                    TrailSectionHeading(
                        title = "我的收藏",
                        subtitle = "和 H5 / 小程序统一按路线卡片 + 进入详情按钮展示。",
                    )
                }

                if (state.favorites.isEmpty()) {
                    item {
                        MineStatusCard(
                            title = "收藏列表为空",
                            body = "当前账号还没有收藏路线，后续会在这里继续承接真实互动结果。",
                        )
                    }
                }
                itemsIndexed(state.favorites) { index, route ->
                    Column(verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.medium)) {
                        TrailRouteShowcaseCard(
                            title = route.title,
                            meta = "${route.distanceKm}km · 收藏 ${route.favoriteCount} · 点位 ${route.waypointCount}",
                            secondaryMeta = "作者 ${route.authorName}",
                            tags = route.tags,
                            accentColors = if (index % 2 == 0) {
                                listOf(Pine900, Pine500)
                            } else {
                                listOf(Ink950, Lake500)
                            },
                        )
                        TrailNotePrimaryButton(title = "进入收藏路线详情", onClick = { onOpenRoute(route.id) })
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.medium)) {
                TrailNotePrimaryButton(
                    title = "编辑个人资料",
                    onClick = {},
                )
                TrailNotePrimaryButton(
                    title = "退出当前账号",
                    onClick = onLogout,
                )
            }
        }
    }
}

private sealed interface MineScreenState {
    data object Loading : MineScreenState

    data class Success(
        val profile: TrailProfile,
        val favorites: List<TrailRouteSummary>,
    ) : MineScreenState

    data class Error(
        val message: String,
    ) : MineScreenState
}

@Composable
private fun StatGrid(
    stats: List<Pair<String, String>>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.medium),
        modifier = Modifier.fillMaxWidth(),
    ) {
        stats.chunked(2).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(TrailNoteSpacing.medium),
                modifier = Modifier.fillMaxWidth(),
            ) {
                rowItems.forEach { (label, value) ->
                    TrailStatCard(
                        label = label,
                        value = value,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun MineStatusCard(
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
