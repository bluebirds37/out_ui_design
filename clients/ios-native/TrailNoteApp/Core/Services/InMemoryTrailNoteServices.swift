import Foundation

struct InMemoryTrailAuthService: TrailAuthServicing {
    func login(account: String, password: String) async throws -> TrailAuthSessionDTO {
        TrailAuthSessionDTO(accessToken: "mock-token-for-bootstrap", nickname: "TrailNote User")
    }
}

struct InMemoryTrailRouteService: TrailRouteServicing {
    private let routes: [TrailRouteSummaryDTO] = [
        TrailRouteSummaryDTO(
            id: 1001,
            title: "莫干山日出山脊线",
            authorId: 201,
            authorName: "景野",
            difficulty: "INTERMEDIATE",
            distanceKm: 8.6,
            durationMinutes: 280,
            ascentM: 540,
            waypointCount: 6,
            favoriteCount: 128,
            tags: ["山脊线", "日出", "机位"]
        ),
        TrailRouteSummaryDTO(
            id: 1002,
            title: "安吉竹海补给环线",
            authorId: 202,
            authorName: "木川",
            difficulty: "BEGINNER",
            distanceKm: 5.2,
            durationMinutes: 165,
            ascentM: 210,
            waypointCount: 4,
            favoriteCount: 72,
            tags: ["竹海", "补给方便"]
        )
    ]

    func featuredRoutes() async throws -> [TrailRouteSummaryDTO] {
        Array(routes.prefix(1))
    }

    func routes(page: Int = 1, pageSize: Int = 10) async throws -> PageEnvelope<TrailRouteSummaryDTO> {
        PageEnvelope(
            records: Array(routes.prefix(pageSize)),
            total: routes.count,
            page: page,
            pageSize: pageSize
        )
    }

    func search(keyword: String) async throws -> TrailSearchResultDTO {
        let filtered = routes.filter { route in
            route.title.localizedCaseInsensitiveContains(keyword) ||
                route.tags.contains(where: { $0.localizedCaseInsensitiveContains(keyword) })
        }
        return TrailSearchResultDTO(keyword: keyword, routes: filtered)
    }

    func routeDetail(routeId: Int) async throws -> TrailRouteDetailDTO {
        let route = routes.first(where: { $0.id == routeId }) ?? routes[0]
        return TrailRouteDetailDTO(
            id: route.id,
            title: route.title,
            authorName: route.authorName,
            authorId: route.authorId,
            description: "这是一条用于客户端联调的示例路线，包含关键点位、收藏和评论链路。",
            difficulty: route.difficulty,
            distanceKm: route.distanceKm,
            durationMinutes: route.durationMinutes,
            ascentM: route.ascentM,
            maxAltitudeM: 1260,
            favoriteCount: route.favoriteCount,
            commentCount: 2,
            favorited: false,
            tags: route.tags,
            waypoints: [
                TrailWaypointSummaryDTO(
                    id: 1,
                    title: "雾海观景台",
                    type: "VIEWPOINT",
                    description: "适合停留拍摄日出和云海。",
                    latitude: 30.0,
                    longitude: 120.0,
                    altitudeM: 980,
                    sortOrder: 1,
                    mediaList: []
                )
            ]
        )
    }

    func toggleRouteFavorite(routeId: Int) async throws -> TrailFavoriteToggleResultDTO {
        let count = routes.first(where: { $0.id == routeId })?.favoriteCount ?? 0
        return TrailFavoriteToggleResultDTO(favorited: true, favoriteCount: count + 1)
    }

    func routeComments(routeId: Int, page: Int, pageSize: Int) async throws -> PageEnvelope<TrailRouteCommentDTO> {
        let comments = [
            TrailRouteCommentDTO(
                id: 1,
                routeId: routeId,
                userId: 1002,
                authorName: "阿屿",
                authorAvatarUrl: nil,
                content: "风口提醒很准，补给点也清晰。",
                mine: false,
                createdAt: "2026-03-29T10:00:00"
            )
        ]
        return PageEnvelope(records: comments, total: comments.count, page: page, pageSize: pageSize)
    }

    func addRouteComment(routeId: Int, content: String) async throws -> TrailCommentCreateResultDTO {
        TrailCommentCreateResultDTO(
            comment: TrailRouteCommentDTO(
                id: 2,
                routeId: routeId,
                userId: 1001,
                authorName: "景野",
                authorAvatarUrl: nil,
                content: content,
                mine: true,
                createdAt: "2026-03-29T10:10:00"
            ),
            commentCount: 3
        )
    }
}

struct InMemoryTrailProfileService: TrailProfileServicing {
    private let favoriteRoutes: [TrailRouteSummaryDTO] = [
        TrailRouteSummaryDTO(
            id: 1001,
            title: "莫干山日出山脊线",
            authorId: 201,
            authorName: "景野",
            difficulty: "INTERMEDIATE",
            distanceKm: 8.6,
            durationMinutes: 280,
            ascentM: 540,
            waypointCount: 6,
            favoriteCount: 128,
            tags: ["山脊线", "日出", "机位"]
        ),
        TrailRouteSummaryDTO(
            id: 1002,
            title: "安吉竹海补给环线",
            authorId: 202,
            authorName: "木川",
            difficulty: "BEGINNER",
            distanceKm: 5.2,
            durationMinutes: 165,
            ascentM: 210,
            waypointCount: 4,
            favoriteCount: 72,
            tags: ["竹海", "补给方便"]
        )
    ]

    func myProfile() async throws -> TrailProfileDTO {
        TrailProfileDTO(
            id: 301,
            nickname: "景野",
            city: "上海",
            levelLabel: "轻中度徒步",
            bio: "偏爱山脊线、湖边营地与日出路线记录",
            publishedRouteCount: 3,
            favoriteRouteCount: 12
        )
    }

    func myFavorites(page: Int, pageSize: Int) async throws -> PageEnvelope<TrailRouteSummaryDTO> {
        PageEnvelope(
            records: Array(favoriteRoutes.prefix(pageSize)),
            total: favoriteRoutes.count,
            page: page,
            pageSize: pageSize
        )
    }
}

struct InMemoryTrailAuthorService: TrailAuthorServicing {
    func authorProfile(authorId: Int) async throws -> TrailAuthorProfileDTO {
        TrailAuthorProfileDTO(
            id: authorId,
            nickname: authorId == 202 ? "木川" : "景野",
            avatarUrl: nil,
            bio: "喜欢轻徒步和湖边路线",
            city: "杭州",
            levelLabel: "新手友好",
            followerCount: 1,
            followingCount: 3,
            publishedRouteCount: 1,
            followed: authorId == 202,
            routes: [
                TrailRouteSummaryDTO(
                    id: 1002,
                    title: "安吉竹海补给环线",
                    authorId: 202,
                    authorName: "木川",
                    difficulty: "BEGINNER",
                    distanceKm: 5.2,
                    durationMinutes: 165,
                    ascentM: 210,
                    waypointCount: 4,
                    favoriteCount: 72,
                    tags: ["竹海", "补给方便"]
                )
            ]
        )
    }

    func toggleAuthorFollow(authorId: Int) async throws -> TrailFollowToggleResultDTO {
        TrailFollowToggleResultDTO(followed: true, followerCount: 2)
    }
}
