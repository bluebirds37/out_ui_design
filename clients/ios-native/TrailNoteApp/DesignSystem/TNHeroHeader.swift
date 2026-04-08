import SwiftUI

struct TNHeroHeader: View {
    let eyebrow: String
    let title: String
    let message: String?

    var body: some View {
        VStack(alignment: .leading, spacing: TNSpacing.small) {
            Text(eyebrow.uppercased())
                .font(TNTypography.caption)
                .foregroundStyle(TNColor.sand500)

            Text(title)
                .font(TNTypography.hero)
                .foregroundStyle(Color.white)
                .fixedSize(horizontal: false, vertical: true)

            if let message {
                Text(message)
                    .font(TNTypography.body)
                    .foregroundStyle(Color.white.opacity(0.82))
                    .fixedSize(horizontal: false, vertical: true)
            }
        }
        .padding(TNSpacing.large)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(
            LinearGradient(
                colors: [TNColor.pine900, TNColor.pine500],
                startPoint: .topLeading,
                endPoint: .bottomTrailing
            )
        )
        .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.sheet, style: .continuous))
    }
}

@ViewBuilder
func tnHeroHeader(eyebrow: String, title: String, message: String? = nil) -> some View {
    TNHeroHeader(eyebrow: eyebrow, title: title, message: message)
}
