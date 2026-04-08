package com.trailnote.android.data.service

import com.trailnote.android.core.network.PageEnvelope

interface AuthService {
    fun login(account: String, password: String): TrailAuthSession
}

interface RouteService {
    fun featuredRoutes(): List<TrailRouteSummary>

    fun routes(page: Int = 1, pageSize: Int = 10): PageEnvelope<TrailRouteSummary>

    fun search(keyword: String): TrailSearchResult

    fun routeDetail(routeId: Long): TrailRouteDetail

    fun toggleRouteFavorite(routeId: Long): TrailFavoriteToggleResult

    fun routeComments(routeId: Long, page: Int = 1, pageSize: Int = 20): PageEnvelope<TrailRouteComment>

    fun addRouteComment(routeId: Long, content: String): TrailCommentCreateResult
}

interface ProfileService {
    fun myProfile(): TrailProfile

    fun myFavorites(page: Int = 1, pageSize: Int = 10): PageEnvelope<TrailRouteSummary>
}

interface AuthorService {
    fun authorProfile(authorId: Long): TrailAuthorProfile

    fun toggleAuthorFollow(authorId: Long): TrailFollowToggleResult
}
