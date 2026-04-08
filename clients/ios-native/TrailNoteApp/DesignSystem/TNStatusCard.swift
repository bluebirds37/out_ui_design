import SwiftUI

struct TNStatusCard: View {
    let title: String
    let message: String

    var body: some View {
        VStack(alignment: .leading, spacing: TNSpacing.small) {
            Text(title)
                .font(TNTypography.title)
                .foregroundStyle(TNColor.ink950)
            Text(message)
                .font(TNTypography.body)
                .foregroundStyle(TNColor.stone600)
        }
        .padding(TNSpacing.medium)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.card, style: .continuous))
        .shadow(color: .black.opacity(TNShadow.cardOpacity), radius: TNShadow.cardRadius, y: TNShadow.cardY)
    }
}
