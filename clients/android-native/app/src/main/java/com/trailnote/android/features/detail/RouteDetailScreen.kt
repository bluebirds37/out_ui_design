package com.trailnote.android.features.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.trailnote.android.data.service.TrailAuthorProfile
import com.trailnote.android.data.service.TrailRouteComment
import com.trailnote.android.data.service.TrailRouteDetail
import com.trailnote.android.data.service.TrailServiceRegistry
import com.trailnote.android.designsystem.components.TrailNotePrimaryButton
import com.trailnote.android.designsystem.components.TrailRouteShowcaseCard
import com.trailnote.android.designsystem.components.TrailSectionHeading
import com.trailnote.android.designsystem.components.TrailStatCard
import com.trailnote.android.designsystem.theme.Mist100
import com.trailnote.android.designsystem.theme.Pine500
import com.trailnote.android.designsystem.theme.Pine900
import com.trailnote.android.designsystem.theme.TrailNoteSpacing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RouteDetailScreen(
    routeId: Long,
    onOpenAuthor: (Long) -> Unit,
) {
    val routeService = TrailServiceRegistry.routeService
    val authorService = TrailServiceRegistry.authorService
    var routeState by remember(routeId) { mutableStateOf<RouteDetailUiState>(RouteDetailUiState.Loading) }
    var commentsState by remember(routeId) { mutableStateOf<CommentListUiState>(CommentListUiState.Loading) }
    var author by remember(routeId) { mutableStateOf<TrailAuthorProfile?>(null) }
    var commentContent by remember(routeId) { mutableStateOf("") }
    var submittingComment by remember(routeId) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    produceState(initialValue = Unit, key1 = routeId) {
        routeState = try {
            val route = withContext(Dispatchers.IO) { routeService.routeDetail(routeId) }
            author = withContext(Dispatchers.IO) { authorService.authorProfile(route.authorId) }
            RouteDetailUiState.Success(route)
        } catch (error: Throwable) {
            RouteDetailUiState.Error(error.message ?: "路线详情加载失败")
        }

        commentsState = try {
            val comments = withContext(Dispatchers.IO) { routeService.routeComments(routeId).records }
            CommentListUiState.Success(comments)
        } catch (error: Throwable) {
            CommentListUiState.Error(error.message ?: "评论加载失败")
        }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.large),
        modifier = Modifier
            .fillMaxSize()
            .background(Mist100)
            .padding(TrailNoteSpacing.large),
    ) {
        when (val currentRouteState = routeState) {
            RouteDetailUiState.Loading -> item { DetailStatusCard("正在加载路线详情", "同步点位、作者信息与评论内容。") }
            is RouteDetailUiState.Error -> item { DetailStatusCard("路线详情加载失败", currentRouteState.message) }
            is RouteDetailUiState.Success -> {
                val route = currentRouteState.route
                item {
                    TrailRouteShowcaseCard(
                        title = route.title,
                        meta = "${route.distanceKm}km · ${formatDuration(route.durationMinutes)} · 爬升 ${route.ascentM}m",
                        secondaryMeta = "路线详情 · 点位 ${route.waypoints.size} · 评论 ${route.commentCount}",
                        tags = route.tags,
                        accentColors = listOf(Pine900, Pine500),
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(TrailNoteSpacing.small)) {
                        TrailNotePrimaryButton(
                            title = if (route.favorited) "已收藏 · ${route.favoriteCount}" else "收藏 · ${route.favoriteCount}",
                            onClick = {
                            scope.launch {
                                routeState = try {
                                    val result = withContext(Dispatchers.IO) {
                                        routeService.toggleRouteFavorite(route.id)
                                    }
                                    RouteDetailUiState.Success(
                                        route.copy(
                                            favorited = result.favorited,
                                            favoriteCount = result.favoriteCount,
                                        ),
                                    )
                                } catch (error: Throwable) {
                                    RouteDetailUiState.Error(error.message ?: "收藏操作失败")
                                }
                            }
                        })

                        author?.let { currentAuthor ->
                            TrailNotePrimaryButton(
                                title = if (currentAuthor.followed) "已关注作者" else "关注作者",
                                onClick = { onOpenAuthor(currentAuthor.id) },
                            )
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.small)) {
                        TrailSectionHeading(
                            title = "路线说明",
                            subtitle = "和其他端统一把说明、作者入口与统计信息放在头图区之后。",
                        )
                        Text(text = route.description, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                author?.let { currentAuthor ->
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            onClick = { onOpenAuthor(currentAuthor.id) },
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.medium),
                                modifier = Modifier.padding(TrailNoteSpacing.large),
                            ) {
                                TrailSectionHeading(
                                    title = "${currentAuthor.nickname} · ${currentAuthor.city ?: "城市未填写"}",
                                    subtitle = "作者信息",
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(TrailNoteSpacing.medium)) {
                                    TrailStatCard(label = "粉丝", value = currentAuthor.followerCount.toString())
                                    TrailStatCard(label = "已发布", value = currentAuthor.publishedRouteCount.toString())
                                }
                            }
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.small)) {
                        TrailSectionHeading(
                            title = "关键点位",
                            subtitle = "统一按点位卡片列表展示类型、海拔和媒体数量。",
                        )
                        route.waypoints.forEach { waypoint ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            ) {
                                Column(modifier = Modifier.padding(TrailNoteSpacing.large)) {
                                    Text(text = waypoint.title, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = "${waypoint.type} · 海拔 ${waypoint.altitudeM ?: 0}m · 媒体 ${waypoint.mediaList.size}",
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                    Text(text = waypoint.description, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.small)) {
                        TrailSectionHeading(
                            title = "路线评论",
                            subtitle = "输入区、提交按钮和最新评论保持同一层级。",
                        )
                        OutlinedTextField(
                            value = commentContent,
                            onValueChange = { commentContent = it },
                            label = { Text("写下你的路线反馈") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        TrailNotePrimaryButton(
                            title = if (submittingComment) "发送中..." else "发布评论",
                            onClick = {
                                if (commentContent.isBlank()) return@TrailNotePrimaryButton
                                scope.launch {
                                    submittingComment = true
                                    try {
                                        val result = withContext(Dispatchers.IO) {
                                            routeService.addRouteComment(route.id, commentContent.trim())
                                        }
                                        routeState = RouteDetailUiState.Success(route.copy(commentCount = result.commentCount))
                                        commentsState = CommentListUiState.Success(
                                            withContext(Dispatchers.IO) { routeService.routeComments(route.id).records },
                                        )
                                        commentContent = ""
                                    } catch (error: Throwable) {
                                        commentsState = CommentListUiState.Error(error.message ?: "评论发送失败")
                                    }
                                    submittingComment = false
                                }
                            },
                        )
                    }
                }
                when (val currentCommentsState = commentsState) {
                    CommentListUiState.Loading -> item { DetailStatusCard("评论同步中", "正在加载当前路线评论。") }
                    is CommentListUiState.Error -> item { DetailStatusCard("评论加载失败", currentCommentsState.message) }
                    is CommentListUiState.Success -> items(currentCommentsState.comments) { comment ->
                        CommentCard(comment)
                    }
                }
            }
        }
    }
}

private sealed interface RouteDetailUiState {
    data object Loading : RouteDetailUiState
    data class Success(val route: TrailRouteDetail) : RouteDetailUiState
    data class Error(val message: String) : RouteDetailUiState
}

private sealed interface CommentListUiState {
    data object Loading : CommentListUiState
    data class Success(val comments: List<TrailRouteComment>) : CommentListUiState
    data class Error(val message: String) : CommentListUiState
}

@Composable
fun DetailStatusCard(title: String, body: String) {
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

@Composable
private fun CommentCard(comment: TrailRouteComment) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(TrailNoteSpacing.small),
            modifier = Modifier.padding(TrailNoteSpacing.large),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = comment.authorName, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = comment.createdAt.take(16).replace("T", " "),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = TrailNoteSpacing.small),
                )
            }
            Text(text = comment.content, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

fun formatDuration(minutes: Int): String {
    val hour = minutes / 60
    val minute = minutes % 60
    return "${hour}h${minute.toString().padStart(2, '0')}m"
}
