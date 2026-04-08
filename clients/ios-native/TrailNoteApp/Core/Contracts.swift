import Foundation

enum TrailRouteDifficulty: String, Codable {
    case beginner = "BEGINNER"
    case intermediate = "INTERMEDIATE"
    case advanced = "ADVANCED"
}

enum TrailViewState: String {
    case idle
    case loading
    case success
    case error
}

enum TrailRecordFlowState: String {
    case idle
    case recording
    case paused
    case ended
}

enum TrailPublishStatus: String, Codable {
    case draft = "DRAFT"
    case pendingReview = "PENDING_REVIEW"
    case published = "PUBLISHED"
    case rejected = "REJECTED"
    case archived = "ARCHIVED"
}

enum TrailInteractionAction: String {
    case toggleFavorite
    case toggleFollow
    case saveDraft
    case submitForReview
}
