import Foundation

struct ApiEnvelope<T: Decodable>: Decodable {
    let success: Bool
    let code: String
    let message: String
    let data: T
}

struct PageEnvelope<T: Decodable>: Decodable {
    let records: [T]
    let total: Int
    let page: Int
    let pageSize: Int
}
