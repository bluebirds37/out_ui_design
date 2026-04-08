import Foundation

enum TrailHTTPMethod: String {
    case get = "GET"
    case post = "POST"
    case put = "PUT"
    case delete = "DELETE"
}

struct NetworkRetryPolicy {
    let retries: Int
    let delayNanoseconds: UInt64
    let retryMethods: Set<TrailHTTPMethod>
    let retryStatuses: Set<Int>

    static let `default` = NetworkRetryPolicy(
        retries: 1,
        delayNanoseconds: 350_000_000,
        retryMethods: [.get],
        retryStatuses: [408, 429, 500, 502, 503, 504]
    )
}

struct NetworkRequest {
    let path: String
    var method: TrailHTTPMethod = .get
    var query: [String: String] = [:]
    var body: Data? = nil
    var headers: [String: String] = [:]
    var timeout: TimeInterval = 8
    var skipAuth: Bool = false
    var retryPolicy: NetworkRetryPolicy = .default
}
