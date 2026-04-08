import Foundation

struct TrailNetworkClient {
    let baseURL: URL
    var accessTokenProvider: () -> String? = { nil }
    var session: URLSession = .shared

    func makeURL(for request: NetworkRequest) -> URL {
        let normalizedPath = request.path.hasPrefix("/") ? String(request.path.dropFirst()) : request.path
        var components = URLComponents(
            url: baseURL.appendingPathComponent(normalizedPath),
            resolvingAgainstBaseURL: false
        )!
        if !request.query.isEmpty {
            components.queryItems = request.query
                .filter { !$0.value.isEmpty }
                .map { URLQueryItem(name: $0.key, value: $0.value) }
        }
        return components.url ?? baseURL.appendingPathComponent(normalizedPath)
    }

    func makeURLRequest(for request: NetworkRequest) -> URLRequest {
        var urlRequest = URLRequest(url: makeURL(for: request))
        urlRequest.httpMethod = request.method.rawValue
        urlRequest.timeoutInterval = request.timeout
        urlRequest.httpBody = request.body
        urlRequest.setValue("application/json", forHTTPHeaderField: "Accept")

        if request.body != nil {
            urlRequest.setValue("application/json", forHTTPHeaderField: "Content-Type")
        }

        if !request.skipAuth, let token = accessTokenProvider(), !token.isEmpty {
            urlRequest.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
        }

        request.headers.forEach { key, value in
            urlRequest.setValue(value, forHTTPHeaderField: key)
        }

        return urlRequest
    }
}
