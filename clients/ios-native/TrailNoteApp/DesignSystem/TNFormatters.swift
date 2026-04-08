import Foundation

extension Int {
    func formattedDuration() -> String {
        let hour = self / 60
        let minute = self % 60
        return "\(hour)h\(String(format: "%02d", minute))m"
    }
}

extension Double {
    func formattedDistance() -> String {
        String(format: "%.1fkm", self)
    }
}

extension String {
    func formattedDifficulty() -> String {
        switch self {
        case "BEGINNER":
            return "新手友好"
        case "INTERMEDIATE":
            return "中阶强度"
        case "ADVANCED":
            return "高阶挑战"
        default:
            return self
        }
    }

    func formattedDateText() -> String {
        guard let date = ISO8601DateFormatter().date(from: self) else {
            return self
        }
        let formatter = DateFormatter()
        formatter.locale = Locale(identifier: "zh_CN")
        formatter.dateFormat = "M月d日 HH:mm"
        return formatter.string(from: date)
    }
}
