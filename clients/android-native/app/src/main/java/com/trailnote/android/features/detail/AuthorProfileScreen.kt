package com.trailnote.android.features.detail

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.trailnote.android.data.service.TrailAuthorProfile
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AuthorProfileScreen(
    authorId: Long,
    onOpenRoute: (Long) -> Unit,
) {
    val authorService = TrailServiceRegistry.authorService
    var state by remember(authorId) { mutableStateOf<AuthorProfileUiState>(AuthorProfileUiState.Loading) }
    val scope = rememberCoroutineScope()

    produceState(initialValue = Unit, key1 = authorId) {
        state = try {
            AuthorProfileUiState.Success(withContext(Dispatchers.IO) { authorService.authorProfile(authorId) })
        } catch (error: Throwable) {
            AuthorProfileUiState.Error(error.message ?: "作者主页加载失败")
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.large),
        modifier = Modifier
            .fillMaxSize()
            .background(Mist100)
            .padding(TrailNoteSpacing.large),
    ) {
        when (val current = state) {
            AuthorProfileUiState.Loading -> item { DetailStatusCard("作者资料同步中", "正在加载作者资料与其公开路线。") }
            is AuthorProfileUiState.Error -> item { DetailStatusCard("作者主页加载失败", current.message) }
            is AuthorProfileUiState.Success -> {
                item {
                    TrailTopHeroCard(
                        eyebrow = "作者主页",
                        title = current.author.nickname,
                        body = "${current.author.city ?: "未填写城市"} · ${current.author.levelLabel ?: "等级未设定"}",
                    )
                }
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.medium),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = current.author.bio ?: "作者暂未补充简介。",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )

                        AuthorStatGrid(
                            stats = listOf(
                                "粉丝" to current.author.followerCount.toString(),
                                "关注中" to current.author.followingCount.toString(),
                                "公开路线" to current.author.publishedRouteCount.toString(),
                                "当前关系" to if (current.author.followed) "已关注" else "未关注",
                            ),
                        )
                    }
                }
                item {
                    TrailNotePrimaryButton(
                        title = if (current.author.followed) {
                            "已关注 · 粉丝 ${current.author.followerCount}"
                        } else {
                            "关注作者 · 粉丝 ${current.author.followerCount}"
                        },
                        onClick = {
                            scope.launch {
                                state = try {
                                    val result = withContext(Dispatchers.IO) {
                                        authorService.toggleAuthorFollow(current.author.id)
                                    }
                                    AuthorProfileUiState.Success(
                                        current.author.copy(
                                            followed = result.followed,
                                            followerCount = result.followerCount,
                                        ),
                                    )
                                } catch (error: Throwable) {
                                    AuthorProfileUiState.Error(error.message ?: "关注操作失败")
                                }
                            }
                        },
                    )
                }
                item {
                    TrailSectionHeading(
                        title = "作者路线",
                        subtitle = "和其他端统一按路线卡片 + 进入详情按钮展示。",
                    )
                }
                itemsIndexed(current.author.routes) { index, route ->
                    Column(verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.medium)) {
                        TrailRouteShowcaseCard(
                            title = route.title,
                            meta = "${route.distanceKm}km · ${formatDuration(route.durationMinutes)} · 收藏 ${route.favoriteCount}",
                            secondaryMeta = "作者路线 ${index + 1}",
                            tags = route.tags,
                            accentColors = if (index % 2 == 0) {
                                listOf(Ink950, Lake500)
                            } else {
                                listOf(Pine900, Pine500)
                            },
                        )
                        TrailNotePrimaryButton(title = "查看路线详情", onClick = { onOpenRoute(route.id) })
                    }
                }
            }
        }
    }
}

private sealed interface AuthorProfileUiState {
    data object Loading : AuthorProfileUiState
    data class Success(val author: TrailAuthorProfile) : AuthorProfileUiState
    data class Error(val message: String) : AuthorProfileUiState
}

@Composable
private fun AuthorStatGrid(
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
