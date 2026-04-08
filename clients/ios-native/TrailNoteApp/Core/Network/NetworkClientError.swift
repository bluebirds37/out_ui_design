import Foundation

struct NetworkClientError: Error, LocalizedError {
    let code: String
    let message: String
    let status: Int
    let requestId: String?

    var errorDescription: String? {
        message
    }
}

extension Notification.Name {
    static let trailSessionExpired = Notification.Name("trail.session.expired")
}
