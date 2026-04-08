package com.trailnote.android.data.service

import com.trailnote.android.core.network.PageEnvelope

class InMemoryAuthService : AuthService {
    override fun login(account: String, password: String): TrailAuthSession {
        return TrailAuthSession(
            accessToken = "mock-token-for-bootstrap",
            nickname = "TrailNote User",
        )
    }
}

class InMemoryRouteService : RouteService {
    private val routes = listOf(
        TrailRouteSummary(
            id = 1001,
            title = "莫干山日出山脊线",
            authorId = 201,
            authorName = "景野",
            difficulty = "INTERMEDIATE",
            distanceKm = 8.6,
            durationMinutes = 280,
            ascentM = 540,
            waypointCount = 6,
            favoriteCount = 128,
            tags = listOf("山脊线", "日出", "机位"),
        ),
        TrailRouteSummary(
            id = 1002,
            title = "安吉竹海补给环线",
            authorId = 202,
            authorName = "木川",
            difficulty = "BEGINNER",
            distanceKm = 5.2,
            durationMinutes = 165,
            ascentM = 210,
            waypointCount = 4,
            favoriteCount = 72,
            tags = listOf("竹海", "补给方便"),
        ),
    )

    override fun featuredRoutes(): List<TrailRouteSummary> = routes.take(1)

    override fun routes(page: Int, pageSize: Int): PageEnvelope<TrailRouteSummary> {
        return PageEnvelope(
            records = routes.take(pageSize),
            total = routes.size,
            page = page,
            pageSize = pageSize,
        )
    }

    override fun search(keyword: String): TrailSearchResult {
        val filteredRoutes = routes.filter { route ->
            route.title.contains(keyword, ignoreCase = true) ||
                route.tags.any { it.contains(keyword, ignoreCase = true) }
        }
        return TrailSearchResult(
            keyword = keyword,
            routes = filteredRoutes,
        )
    }

    override fun routeDetail(routeId: Long): TrailRouteDetail {
        val route = routes.firstOrNull { it.id == routeId } ?: routes.first()
        return TrailRouteDetail(
            id = route.id,
            title = route.title,
            authorId = route.authorId,
            authorName = route.authorName,
            description = "这是一条用于客户端联调的示例路线，包含关键点位、收藏和评论链路。",
            difficulty = route.difficulty,
            distanceKm = route.distanceKm,
            durationMinutes = route.durationMinutes,
            ascentM = route.ascentM,
            maxAltitudeM = 1260,
            favoriteCount = route.favoriteCount,
            commentCount = 2,
            favorited = false,
            tags = route.tags,
            waypoints = listOf(
                TrailWaypointSummary(
                    id = 1,
                    title = "雾海观景台",
                    type = "VIEWPOINT",
                    description = "适合停留拍摄日出和云海。",
                    latitude = 30.0,
                    longitude = 120.0,
                    altitudeM = 980,
                    sortOrder = 1,
                    mediaList = emptyList(),
                ),
            ),
        )
    }

    override fun toggleRouteFavorite(routeId: Long): TrailFavoriteToggleResult {
        val count = routes.firstOrNull { it.id == routeId }?.favoriteCount ?: 0
        return TrailFavoriteToggleResult(favorited = true, favoriteCount = count + 1)
    }

    override fun routeComments(routeId: Long, page: Int, pageSize: Int): PageEnvelope<TrailRouteComment> {
        val comments = listOf(
            TrailRouteComment(
                id = 1,
                routeId = routeId,
                userId = 1002,
                authorName = "阿屿",
                authorAvatarUrl = null,
                content = "风口提醒很准，补给点也清晰。",
                mine = false,
                createdAt = "2026-03-29T10:00:00",
            ),
        )
        return PageEnvelope(records = comments, total = comments.size, page = page, pageSize = pageSize)
    }

    override fun addRouteComment(routeId: Long, content: String): TrailCommentCreateResult {
        return TrailCommentCreateResult(
            comment = TrailRouteComment(
                id = 2,
                routeId = routeId,
                userId = 1001,
                authorName = "景野",
                authorAvatarUrl = null,
                content = content,
                mine = true,
                createdAt = "2026-03-29T10:10:00",
            ),
            commentCount = 3,
        )
    }
}

class InMemoryProfileService : ProfileService {
    private val favoriteRoutes = listOf(
        TrailRouteSummary(
            id = 1001,
            title = "莫干山日出山脊线",
            authorId = 201,
            authorName = "景野",
            difficulty = "INTERMEDIATE",
            distanceKm = 8.6,
            durationMinutes = 280,
            ascentM = 540,
            waypointCount = 6,
            favoriteCount = 128,
            tags = listOf("山脊线", "日出", "机位"),
        ),
        TrailRouteSummary(
            id = 1002,
            title = "安吉竹海补给环线",
            authorId = 202,
            authorName = "木川",
            difficulty = "BEGINNER",
            distanceKm = 5.2,
            durationMinutes = 165,
            ascentM = 210,
            waypointCount = 4,
            favoriteCount = 72,
            tags = listOf("竹海", "补给方便"),
        ),
    )

    override fun myProfile(): TrailProfile {
        return TrailProfile(
            id = 301,
            nickname = "景野",
            city = "上海",
            levelLabel = "轻中度徒步",
            bio = "偏爱山脊线、湖边营地与日出路线记录",
            publishedRouteCount = 3,
            favoriteRouteCount = 12,
        )
    }

    override fun myFavorites(page: Int, pageSize: Int): PageEnvelope<TrailRouteSummary> {
        return PageEnvelope(
            records = favoriteRoutes.take(pageSize),
            total = favoriteRoutes.size,
            page = page,
            pageSize = pageSize,
        )
    }
}

class InMemoryAuthorService : AuthorService {
    override fun authorProfile(authorId: Long): TrailAuthorProfile {
        return TrailAuthorProfile(
            id = authorId,
            nickname = if (authorId == 202L) "木川" else "景野",
            avatarUrl = null,
            bio = "喜欢轻徒步和湖边路线",
            city = "杭州",
            levelLabel = "新手友好",
            followerCount = 1,
            followingCount = 3,
            publishedRouteCount = 1,
            followed = authorId == 202L,
            routes = listOf(
                TrailRouteSummary(
                    id = 1002,
                    title = "安吉竹海补给环线",
                    authorId = 202,
                    authorName = "木川",
                    difficulty = "BEGINNER",
                    distanceKm = 5.2,
                    durationMinutes = 165,
                    ascentM = 210,
                    waypointCount = 4,
                    favoriteCount = 72,
                    tags = listOf("竹海", "补给方便"),
                ),
            ),
        )
    }

    override fun toggleAuthorFollow(authorId: Long): TrailFollowToggleResult {
        return TrailFollowToggleResult(followed = true, followerCount = 2)
    }
}
