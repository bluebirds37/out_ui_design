import Foundation

enum TrailServiceRegistry {
    private static let networkClient = TrailNetworkClient(
        baseURL: TrailEnvironment.apiBaseURL,
        accessTokenProvider: {
            UserDefaults.standard.string(forKey: TrailEnvironment.accessTokenStorageKey)
        }
    )

    static var authService: any TrailAuthServicing {
        if TrailEnvironment.useMockData {
            return InMemoryTrailAuthService()
        }
        return RemoteTrailAuthService(networkClient: networkClient)
    }

    static var routeService: any TrailRouteServicing {
        if TrailEnvironment.useMockData {
            return InMemoryTrailRouteService()
        }
        return RemoteTrailRouteService(networkClient: networkClient)
    }

    static var profileService: any TrailProfileServicing {
        if TrailEnvironment.useMockData {
            return InMemoryTrailProfileService()
        }
        return RemoteTrailProfileService(networkClient: networkClient)
    }

    static var authorService: any TrailAuthorServicing {
        if TrailEnvironment.useMockData {
            return InMemoryTrailAuthorService()
        }
        return RemoteTrailAuthorService(networkClient: networkClient)
    }
}
