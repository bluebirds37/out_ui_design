import Foundation

struct TrailAuthSessionDTO: Decodable {
    let accessToken: String
    let nickname: String
}

struct TrailRouteSummaryDTO: Identifiable, Decodable {
    let id: Int
    let title: String
    let authorId: Int
    let authorName: String
    let difficulty: String
    let distanceKm: Double
    let durationMinutes: Int
    let ascentM: Int
    let waypointCount: Int
    let favoriteCount: Int
    let tags: [String]
}

struct TrailWaypointMediaDTO: Identifiable, Decodable {
    let id: Int
    let mediaType: String
    let coverUrl: String?
    let mediaUrl: String
    let durationSeconds: Int?
}

struct TrailWaypointSummaryDTO: Identifiable, Decodable {
    let id: Int
    let title: String
    let type: String
    let description: String
    let latitude: Double
    let longitude: Double
    let altitudeM: Int?
    let sortOrder: Int
    let mediaList: [TrailWaypointMediaDTO]
}

struct TrailRouteDetailDTO: Identifiable, Decodable {
    let id: Int
    let title: String
    let authorName: String
    let authorId: Int
    let description: String
    let difficulty: String
    let distanceKm: Double
    let durationMinutes: Int
    let ascentM: Int
    let maxAltitudeM: Int
    let favoriteCount: Int
    let commentCount: Int
    let favorited: Bool
    let tags: [String]
    let waypoints: [TrailWaypointSummaryDTO]
}

struct TrailFavoriteToggleResultDTO: Decodable {
    let favorited: Bool
    let favoriteCount: Int
}

struct TrailRouteCommentDTO: Identifiable, Decodable {
    let id: Int
    let routeId: Int
    let userId: Int
    let authorName: String
    let authorAvatarUrl: String?
    let content: String
    let mine: Bool
    let createdAt: String
}

struct TrailCommentCreateResultDTO: Decodable {
    let comment: TrailRouteCommentDTO
    let commentCount: Int
}

struct TrailProfileDTO: Decodable {
    let id: Int
    let nickname: String
    let city: String
    let levelLabel: String
    let bio: String
    let publishedRouteCount: Int
    let favoriteRouteCount: Int
}

struct TrailSearchResultDTO: Decodable {
    let keyword: String
    let routes: [TrailRouteSummaryDTO]
}

struct TrailAuthorProfileDTO: Decodable {
    let id: Int
    let nickname: String
    let avatarUrl: String?
    let bio: String?
    let city: String?
    let levelLabel: String?
    let followerCount: Int
    let followingCount: Int
    let publishedRouteCount: Int
    let followed: Bool
    let routes: [TrailRouteSummaryDTO]
}

struct TrailFollowToggleResultDTO: Decodable {
    let followed: Bool
    let followerCount: Int
}
