import SwiftUI

struct TNPrimaryButton: View {
    let title: String
    let systemImage: String?
    var action: () -> Void

    var body: some View {
        Button(action: action) {
            HStack(spacing: TNSpacing.small) {
                if let systemImage {
                    Image(systemName: systemImage)
                }
                Text(title)
                    .font(TNTypography.body.weight(.semibold))
            }
            .foregroundStyle(Color.white)
            .frame(maxWidth: .infinity)
            .padding(.vertical, TNSpacing.medium)
            .background(TNColor.pine700)
            .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.card, style: .continuous))
        }
        .buttonStyle(.plain)
    }
}

#Preview {
    TNPrimaryButton(title: "开始记录", systemImage: "record.circle.fill") {}
        .padding()
        .background(TNColor.mist100)
}
