package com.trailnote.android.data.service

import com.trailnote.android.core.network.ApiEnvelope
import com.trailnote.android.core.network.HttpMethod
import com.trailnote.android.core.network.NetworkError
import com.trailnote.android.core.network.NetworkException
import com.trailnote.android.core.network.PageEnvelope
import com.trailnote.android.core.network.RequestOptions
import com.trailnote.android.core.network.TrailNoteHttpClient
import com.trailnote.android.session.TrailSessionStore
import org.json.JSONArray
import org.json.JSONObject

class RemoteAuthService(
    private val httpClient: TrailNoteHttpClient,
) : AuthService {
    override fun login(account: String, password: String): TrailAuthSession {
        val payload = httpClient.execute(
            RequestOptions(
                method = HttpMethod.POST,
                path = "/api/auth/login",
                body = JSONObject(mapOf("account" to account, "password" to password)).toString(),
                skipAuth = true,
            ),
        )
        return payload.getJSONObject("data").toAuthSession()
    }
}

class RemoteRouteService(
    private val httpClient: TrailNoteHttpClient,
) : RouteService {
    override fun featuredRoutes(): List<TrailRouteSummary> {
        val payload = httpClient.execute(RequestOptions(path = "/api/routes/featured"))
        return payload.getJSONArray("data").toRouteSummaryList()
    }

    override fun routes(page: Int, pageSize: Int): PageEnvelope<TrailRouteSummary> {
        val payload = httpClient.execute(
            RequestOptions(
                path = "/api/routes",
                query = mapOf(
                    "page" to page,
                    "pageSize" to pageSize,
                ),
            ),
        )
        return payload.getJSONObject("data").toRouteSummaryPage()
    }

    override fun search(keyword: String): TrailSearchResult {
        val payload = httpClient.execute(
            RequestOptions(
                path = "/api/search",
                query = mapOf("q" to keyword),
            ),
        )
        return payload.getJSONObject("data").toSearchResult()
    }

    override fun routeDetail(routeId: Long): TrailRouteDetail {
        val payload = httpClient.execute(RequestOptions(path = "/api/routes/$routeId"))
        return payload.getJSONObject("data").toRouteDetail()
    }

    override fun toggleRouteFavorite(routeId: Long): TrailFavoriteToggleResult {
        val payload = httpClient.execute(
            RequestOptions(
                method = HttpMethod.POST,
                path = "/api/routes/$routeId/favorite",
            ),
        )
        return payload.getJSONObject("data").toFavoriteToggleResult()
    }

    override fun routeComments(routeId: Long, page: Int, pageSize: Int): PageEnvelope<TrailRouteComment> {
        val payload = httpClient.execute(
            RequestOptions(
                path = "/api/routes/$routeId/comments",
                query = mapOf("page" to page, "pageSize" to pageSize),
            ),
        )
        return payload.getJSONObject("data").toCommentPage()
    }

    override fun addRouteComment(routeId: Long, content: String): TrailCommentCreateResult {
        val payload = httpClient.execute(
            RequestOptions(
                method = HttpMethod.POST,
                path = "/api/routes/$routeId/comments",
                body = JSONObject(mapOf("content" to content)).toString(),
            ),
        )
        return payload.getJSONObject("data").toCommentCreateResult()
    }
}

class RemoteProfileService(
    private val httpClient: TrailNoteHttpClient,
) : ProfileService {
    override fun myProfile(): TrailProfile {
        val payload = httpClient.execute(RequestOptions(path = "/api/me"))
        return payload.getJSONObject("data").toProfile()
    }

    override fun myFavorites(page: Int, pageSize: Int): PageEnvelope<TrailRouteSummary> {
        val payload = httpClient.execute(
            RequestOptions(
                path = "/api/me/favorites",
                query = mapOf(
                    "page" to page,
                    "pageSize" to pageSize,
                ),
            ),
        )
        return payload.getJSONObject("data").toRouteSummaryPage()
    }
}

class RemoteAuthorService(
    private val httpClient: TrailNoteHttpClient,
) : AuthorService {
    override fun authorProfile(authorId: Long): TrailAuthorProfile {
        val payload = httpClient.execute(RequestOptions(path = "/api/authors/$authorId"))
        return payload.getJSONObject("data").toAuthorProfile()
    }

    override fun toggleAuthorFollow(authorId: Long): TrailFollowToggleResult {
        val payload = httpClient.execute(
            RequestOptions(
                method = HttpMethod.POST,
                path = "/api/authors/$authorId/follow",
            ),
        )
        return payload.getJSONObject("data").toFollowToggleResult()
    }
}

private fun TrailNoteHttpClient.execute(options: RequestOptions): JSONObject {
    val connection = buildConnection(options)
    return try {
        if (options.body != null) {
            connection.outputStream.bufferedWriter().use { writer ->
                writer.write(options.body)
            }
        }

        val status = connection.responseCode
        val body = runCatching {
            val stream = if (status in 200..299) connection.inputStream else connection.errorStream
            stream?.bufferedReader()?.use { it.readText() }.orEmpty()
        }.getOrDefault("")

        val payload = if (body.isBlank()) JSONObject() else JSONObject(body)
        if (status !in 200..299 || !payload.optBoolean("success", false)) {
            if (status == 401 || payload.optString("code") == "UNAUTHORIZED") {
                TrailSessionStore.clearUnauthorizedSession()
            }
            throw NetworkException(
                NetworkError(
                    code = payload.optString("code", "HTTP_$status"),
                    message = payload.optString("message", "HTTP $status"),
                    status = status,
                    requestId = connection.getHeaderField("x-request-id"),
                ),
            )
        }
        payload
    } finally {
        connection.disconnect()
    }
}

private fun JSONObject.toRouteSummaryPage(): PageEnvelope<TrailRouteSummary> {
    return PageEnvelope(
        records = getJSONArray("records").toRouteSummaryList(),
        total = optInt("total"),
        page = optInt("page"),
        pageSize = optInt("pageSize"),
    )
}

private fun JSONObject.toAuthSession(): TrailAuthSession {
    return TrailAuthSession(
        accessToken = optString("accessToken"),
        nickname = optString("nickname"),
    )
}

private fun JSONObject.toSearchResult(): TrailSearchResult {
    return TrailSearchResult(
        keyword = optString("keyword"),
        routes = optJSONArray("routes")?.toRouteSummaryList().orEmpty(),
    )
}

private fun JSONObject.toRouteDetail(): TrailRouteDetail {
    return TrailRouteDetail(
        id = optLong("id"),
        title = optString("title"),
        authorId = optLong("authorId"),
        authorName = optString("authorName"),
        description = optString("description"),
        difficulty = optString("difficulty"),
        distanceKm = optDouble("distanceKm"),
        durationMinutes = optInt("durationMinutes"),
        ascentM = optInt("ascentM"),
        maxAltitudeM = optInt("maxAltitudeM"),
        favoriteCount = optInt("favoriteCount"),
        commentCount = optInt("commentCount"),
        favorited = optBoolean("favorited"),
        tags = optJSONArray("tags")?.toStringList().orEmpty(),
        waypoints = optJSONArray("waypoints")?.toWaypointList().orEmpty(),
    )
}

private fun JSONObject.toFavoriteToggleResult(): TrailFavoriteToggleResult {
    return TrailFavoriteToggleResult(
        favorited = optBoolean("favorited"),
        favoriteCount = optInt("favoriteCount"),
    )
}

private fun JSONObject.toCommentPage(): PageEnvelope<TrailRouteComment> {
    return PageEnvelope(
        records = getJSONArray("records").toCommentList(),
        total = optInt("total"),
        page = optInt("page"),
        pageSize = optInt("pageSize"),
    )
}

private fun JSONObject.toCommentCreateResult(): TrailCommentCreateResult {
    return TrailCommentCreateResult(
        comment = getJSONObject("comment").toComment(),
        commentCount = optInt("commentCount"),
    )
}

private fun JSONObject.toProfile(): TrailProfile {
    return TrailProfile(
        id = optLong("id"),
        nickname = optString("nickname"),
        city = optString("city"),
        levelLabel = optString("levelLabel"),
        bio = optString("bio"),
        publishedRouteCount = optInt("publishedRouteCount"),
        favoriteRouteCount = optInt("favoriteRouteCount"),
    )
}

private fun JSONObject.toAuthorProfile(): TrailAuthorProfile {
    return TrailAuthorProfile(
        id = optLong("id"),
        nickname = optString("nickname"),
        avatarUrl = optNullableString("avatarUrl"),
        bio = optNullableString("bio"),
        city = optNullableString("city"),
        levelLabel = optNullableString("levelLabel"),
        followerCount = optInt("followerCount"),
        followingCount = optInt("followingCount"),
        publishedRouteCount = optInt("publishedRouteCount"),
        followed = optBoolean("followed"),
        routes = optJSONArray("routes")?.toRouteSummaryList().orEmpty(),
    )
}

private fun JSONObject.toFollowToggleResult(): TrailFollowToggleResult {
    return TrailFollowToggleResult(
        followed = optBoolean("followed"),
        followerCount = optInt("followerCount"),
    )
}

private fun JSONArray.toRouteSummaryList(): List<TrailRouteSummary> {
    return buildList {
        for (index in 0 until length()) {
            add(getJSONObject(index).toRouteSummary())
        }
    }
}

private fun JSONObject.toRouteSummary(): TrailRouteSummary {
    return TrailRouteSummary(
        id = optLong("id"),
        title = optString("title"),
        authorId = optLong("authorId"),
        authorName = optString("authorName"),
        difficulty = optString("difficulty"),
        distanceKm = optDouble("distanceKm"),
        durationMinutes = optInt("durationMinutes"),
        ascentM = optInt("ascentM"),
        waypointCount = optInt("waypointCount"),
        favoriteCount = optInt("favoriteCount"),
        tags = optJSONArray("tags")?.toStringList().orEmpty(),
    )
}

private fun JSONArray.toWaypointList(): List<TrailWaypointSummary> {
    return buildList {
        for (index in 0 until length()) {
            add(getJSONObject(index).toWaypoint())
        }
    }
}

private fun JSONObject.toWaypoint(): TrailWaypointSummary {
    return TrailWaypointSummary(
        id = optLong("id"),
        title = optString("title"),
        type = optString("type"),
        description = optString("description"),
        latitude = optDouble("latitude"),
        longitude = optDouble("longitude"),
        altitudeM = optNullableInt("altitudeM"),
        sortOrder = optInt("sortOrder"),
        mediaList = optJSONArray("mediaList")?.toMediaList().orEmpty(),
    )
}

private fun JSONArray.toMediaList(): List<TrailWaypointMedia> {
    return buildList {
        for (index in 0 until length()) {
            add(getJSONObject(index).toMedia())
        }
    }
}

private fun JSONObject.toMedia(): TrailWaypointMedia {
    return TrailWaypointMedia(
        id = optLong("id"),
        mediaType = optString("mediaType"),
        coverUrl = optNullableString("coverUrl"),
        mediaUrl = optString("mediaUrl"),
        durationSeconds = optNullableInt("durationSeconds"),
    )
}

private fun JSONArray.toCommentList(): List<TrailRouteComment> {
    return buildList {
        for (index in 0 until length()) {
            add(getJSONObject(index).toComment())
        }
    }
}

private fun JSONObject.toComment(): TrailRouteComment {
    return TrailRouteComment(
        id = optLong("id"),
        routeId = optLong("routeId"),
        userId = optLong("userId"),
        authorName = optString("authorName"),
        authorAvatarUrl = optNullableString("authorAvatarUrl"),
        content = optString("content"),
        mine = optBoolean("mine"),
        createdAt = optString("createdAt"),
    )
}

private fun JSONArray.toStringList(): List<String> {
    return buildList {
        for (index in 0 until length()) {
            add(optString(index))
        }
    }
}

private fun JSONObject.optNullableString(key: String): String? = if (isNull(key)) null else optString(key)

private fun JSONObject.optNullableInt(key: String): Int? = if (isNull(key)) null else optInt(key)
