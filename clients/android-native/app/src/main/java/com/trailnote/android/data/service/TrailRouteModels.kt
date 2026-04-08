package com.trailnote.android.data.service

data class TrailAuthSession(
    val accessToken: String,
    val nickname: String,
)

data class TrailRouteSummary(
    val id: Long,
    val title: String,
    val authorId: Long,
    val authorName: String,
    val difficulty: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val ascentM: Int,
    val waypointCount: Int,
    val favoriteCount: Int,
    val tags: List<String>,
)

data class TrailWaypointMedia(
    val id: Long,
    val mediaType: String,
    val coverUrl: String?,
    val mediaUrl: String,
    val durationSeconds: Int?,
)

data class TrailWaypointSummary(
    val id: Long,
    val title: String,
    val type: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val altitudeM: Int?,
    val sortOrder: Int,
    val mediaList: List<TrailWaypointMedia>,
)

data class TrailRouteDetail(
    val id: Long,
    val title: String,
    val authorId: Long,
    val authorName: String,
    val description: String,
    val difficulty: String,
    val distanceKm: Double,
    val durationMinutes: Int,
    val ascentM: Int,
    val maxAltitudeM: Int,
    val favoriteCount: Int,
    val commentCount: Int,
    val favorited: Boolean,
    val tags: List<String>,
    val waypoints: List<TrailWaypointSummary>,
)

data class TrailFavoriteToggleResult(
    val favorited: Boolean,
    val favoriteCount: Int,
)

data class TrailRouteComment(
    val id: Long,
    val routeId: Long,
    val userId: Long,
    val authorName: String,
    val authorAvatarUrl: String?,
    val content: String,
    val mine: Boolean,
    val createdAt: String,
)

data class TrailCommentCreateResult(
    val comment: TrailRouteComment,
    val commentCount: Int,
)

data class TrailProfile(
    val id: Long,
    val nickname: String,
    val city: String,
    val levelLabel: String,
    val bio: String,
    val publishedRouteCount: Int,
    val favoriteRouteCount: Int,
)

data class TrailSearchResult(
    val keyword: String,
    val routes: List<TrailRouteSummary>,
)

data class TrailAuthorProfile(
    val id: Long,
    val nickname: String,
    val avatarUrl: String?,
    val bio: String?,
    val city: String?,
    val levelLabel: String?,
    val followerCount: Int,
    val followingCount: Int,
    val publishedRouteCount: Int,
    val followed: Boolean,
    val routes: List<TrailRouteSummary>,
)

data class TrailFollowToggleResult(
    val followed: Boolean,
    val followerCount: Int,
)
