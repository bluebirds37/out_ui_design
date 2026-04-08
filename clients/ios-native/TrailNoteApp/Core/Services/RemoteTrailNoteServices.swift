import Foundation

private func notifyUnauthorizedIfNeeded(status: Int, code: String) {
    if status == 401 || code == "UNAUTHORIZED" {
        NotificationCenter.default.post(name: .trailSessionExpired, object: nil)
    }
}

struct RemoteTrailAuthService: TrailAuthServicing {
    private let networkClient: TrailNetworkClient

    init(networkClient: TrailNetworkClient) {
        self.networkClient = networkClient
    }

    func login(account: String, password: String) async throws -> TrailAuthSessionDTO {
        let body = try JSONEncoder().encode(LoginBody(account: account, password: password))
        return try await fetch(
            TrailAuthSessionDTO.self,
            with: NetworkRequest(path: "/api/auth/login", method: .post, body: body, skipAuth: true)
        )
    }

    private func fetch<T: Decodable>(_ type: T.Type, with request: NetworkRequest) async throws -> T {
        let urlRequest = networkClient.makeURLRequest(for: request)
        let (data, response) = try await networkClient.session.data(for: urlRequest)
        let httpResponse = response as? HTTPURLResponse
        let status = httpResponse?.statusCode ?? 0
        let envelope = try JSONDecoder().decode(ApiEnvelope<T>.self, from: data)

        guard (200..<300).contains(status), envelope.success else {
            notifyUnauthorizedIfNeeded(status: status, code: envelope.code)
            throw NetworkClientError(
                code: envelope.code,
                message: envelope.message,
                status: status,
                requestId: httpResponse?.value(forHTTPHeaderField: "x-request-id")
            )
        }

        return envelope.data
    }

    private struct LoginBody: Encodable {
        let account: String
        let password: String
    }
}

struct RemoteTrailRouteService: TrailRouteServicing {
    private let networkClient: TrailNetworkClient

    init(networkClient: TrailNetworkClient) {
        self.networkClient = networkClient
    }

    func featuredRoutes() async throws -> [TrailRouteSummaryDTO] {
        let request = NetworkRequest(path: "/api/routes/featured")
        return try await fetch([TrailRouteSummaryDTO].self, with: request)
    }

    func routes(page: Int, pageSize: Int) async throws -> PageEnvelope<TrailRouteSummaryDTO> {
        let request = NetworkRequest(
            path: "/api/routes",
            query: [
                "page": String(page),
                "pageSize": String(pageSize),
            ]
        )
        return try await fetch(PageEnvelope<TrailRouteSummaryDTO>.self, with: request)
    }

    func search(keyword: String) async throws -> TrailSearchResultDTO {
        let request = NetworkRequest(
            path: "/api/search",
            query: ["q": keyword]
        )
        return try await fetch(TrailSearchResultDTO.self, with: request)
    }

    func routeDetail(routeId: Int) async throws -> TrailRouteDetailDTO {
        try await fetch(TrailRouteDetailDTO.self, with: NetworkRequest(path: "/api/routes/\(routeId)"))
    }

    func toggleRouteFavorite(routeId: Int) async throws -> TrailFavoriteToggleResultDTO {
        try await fetch(
            TrailFavoriteToggleResultDTO.self,
            with: NetworkRequest(path: "/api/routes/\(routeId)/favorite", method: .post)
        )
    }

    func routeComments(routeId: Int, page: Int, pageSize: Int) async throws -> PageEnvelope<TrailRouteCommentDTO> {
        try await fetch(
            PageEnvelope<TrailRouteCommentDTO>.self,
            with: NetworkRequest(
                path: "/api/routes/\(routeId)/comments",
                query: [
                    "page": String(page),
                    "pageSize": String(pageSize)
                ]
            )
        )
    }

    func addRouteComment(routeId: Int, content: String) async throws -> TrailCommentCreateResultDTO {
        let body = try JSONEncoder().encode(CommentBody(content: content))
        return try await fetch(
            TrailCommentCreateResultDTO.self,
            with: NetworkRequest(path: "/api/routes/\(routeId)/comments", method: .post, body: body)
        )
    }

    private func fetch<T: Decodable>(_ type: T.Type, with request: NetworkRequest) async throws -> T {
        let urlRequest = networkClient.makeURLRequest(for: request)
        let (data, response) = try await networkClient.session.data(for: urlRequest)
        let httpResponse = response as? HTTPURLResponse
        let status = httpResponse?.statusCode ?? 0

        let decoder = JSONDecoder()
        let envelope = try decoder.decode(ApiEnvelope<T>.self, from: data)
        guard (200..<300).contains(status), envelope.success else {
            notifyUnauthorizedIfNeeded(status: status, code: envelope.code)
            throw NetworkClientError(
                code: envelope.code,
                message: envelope.message,
                status: status,
                requestId: httpResponse?.value(forHTTPHeaderField: "x-request-id")
            )
        }

        return envelope.data
    }

    private struct CommentBody: Encodable {
        let content: String
    }
}

struct RemoteTrailProfileService: TrailProfileServicing {
    private let networkClient: TrailNetworkClient

    init(networkClient: TrailNetworkClient) {
        self.networkClient = networkClient
    }

    func myProfile() async throws -> TrailProfileDTO {
        try await fetch(TrailProfileDTO.self, with: NetworkRequest(path: "/api/me"))
    }

    func myFavorites(page: Int, pageSize: Int) async throws -> PageEnvelope<TrailRouteSummaryDTO> {
        try await fetch(
            PageEnvelope<TrailRouteSummaryDTO>.self,
            with: NetworkRequest(
                path: "/api/me/favorites",
                query: [
                    "page": String(page),
                    "pageSize": String(pageSize),
                ]
            )
        )
    }

    private func fetch<T: Decodable>(_ type: T.Type, with request: NetworkRequest) async throws -> T {
        let urlRequest = networkClient.makeURLRequest(for: request)
        let (data, response) = try await networkClient.session.data(for: urlRequest)
        let httpResponse = response as? HTTPURLResponse
        let status = httpResponse?.statusCode ?? 0

        let envelope = try JSONDecoder().decode(ApiEnvelope<T>.self, from: data)
        guard (200..<300).contains(status), envelope.success else {
            notifyUnauthorizedIfNeeded(status: status, code: envelope.code)
            throw NetworkClientError(
                code: envelope.code,
                message: envelope.message,
                status: status,
                requestId: httpResponse?.value(forHTTPHeaderField: "x-request-id")
            )
        }

        return envelope.data
    }
}

struct RemoteTrailAuthorService: TrailAuthorServicing {
    private let networkClient: TrailNetworkClient

    init(networkClient: TrailNetworkClient) {
        self.networkClient = networkClient
    }

    func authorProfile(authorId: Int) async throws -> TrailAuthorProfileDTO {
        try await fetch(TrailAuthorProfileDTO.self, with: NetworkRequest(path: "/api/authors/\(authorId)"))
    }

    func toggleAuthorFollow(authorId: Int) async throws -> TrailFollowToggleResultDTO {
        try await fetch(
            TrailFollowToggleResultDTO.self,
            with: NetworkRequest(path: "/api/authors/\(authorId)/follow", method: .post)
        )
    }

    private func fetch<T: Decodable>(_ type: T.Type, with request: NetworkRequest) async throws -> T {
        let urlRequest = networkClient.makeURLRequest(for: request)
        let (data, response) = try await networkClient.session.data(for: urlRequest)
        let httpResponse = response as? HTTPURLResponse
        let status = httpResponse?.statusCode ?? 0
        let envelope = try JSONDecoder().decode(ApiEnvelope<T>.self, from: data)
        guard (200..<300).contains(status), envelope.success else {
            throw NetworkClientError(
                code: envelope.code,
                message: envelope.message,
                status: status,
                requestId: httpResponse?.value(forHTTPHeaderField: "x-request-id")
            )
        }
        return envelope.data
    }
}
