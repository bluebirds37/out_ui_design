import Foundation

protocol TrailAuthServicing {
    func login(account: String, password: String) async throws -> TrailAuthSessionDTO
}

protocol TrailRouteServicing {
    func featuredRoutes() async throws -> [TrailRouteSummaryDTO]
    func routes(page: Int, pageSize: Int) async throws -> PageEnvelope<TrailRouteSummaryDTO>
    func search(keyword: String) async throws -> TrailSearchResultDTO
    func routeDetail(routeId: Int) async throws -> TrailRouteDetailDTO
    func toggleRouteFavorite(routeId: Int) async throws -> TrailFavoriteToggleResultDTO
    func routeComments(routeId: Int, page: Int, pageSize: Int) async throws -> PageEnvelope<TrailRouteCommentDTO>
    func addRouteComment(routeId: Int, content: String) async throws -> TrailCommentCreateResultDTO
}

protocol TrailProfileServicing {
    func myProfile() async throws -> TrailProfileDTO
    func myFavorites(page: Int, pageSize: Int) async throws -> PageEnvelope<TrailRouteSummaryDTO>
}

protocol TrailAuthorServicing {
    func authorProfile(authorId: Int) async throws -> TrailAuthorProfileDTO
    func toggleAuthorFollow(authorId: Int) async throws -> TrailFollowToggleResultDTO
}
