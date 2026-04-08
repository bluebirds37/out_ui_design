import Foundation

enum TrailEnvironment {
    static let apiBaseURL = URL(string: "http://127.0.0.1:8080")!
    static let useMockData = false
    static let accessTokenStorageKey = "trailnote_access_token"
}
