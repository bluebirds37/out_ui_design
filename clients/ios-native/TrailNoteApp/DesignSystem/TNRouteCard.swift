import SwiftUI

struct TNRouteCard: View {
    let title: String
    let meta: String
    let tags: [String]

    var body: some View {
        VStack(alignment: .leading, spacing: TNSpacing.medium) {
            RoundedRectangle(cornerRadius: TNCornerRadius.card, style: .continuous)
                .fill(
                    LinearGradient(
                        colors: [TNColor.pine900, TNColor.pine500],
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                .frame(height: 160)
                .overlay(alignment: .topLeading) {
                    Text("TRAILNOTE")
                        .font(TNTypography.caption)
                        .foregroundStyle(.white.opacity(0.85))
                        .padding(TNSpacing.medium)
                }

            VStack(alignment: .leading, spacing: TNSpacing.small) {
                Text(title)
                    .font(TNTypography.title)
                    .foregroundStyle(TNColor.ink950)

                Text(meta)
                    .font(TNTypography.body)
                    .foregroundStyle(TNColor.stone600)

                HStack(spacing: TNSpacing.xSmall) {
                    ForEach(tags, id: \.self) { tag in
                        Text(tag)
                            .font(TNTypography.caption)
                            .foregroundStyle(TNColor.pine700)
                            .padding(.horizontal, TNSpacing.small)
                            .padding(.vertical, TNSpacing.xSmall)
                            .background(TNColor.mist100)
                            .clipShape(Capsule())
                    }
                }
            }
        }
        .padding(TNSpacing.medium)
        .background(Color.white)
        .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.card, style: .continuous))
        .shadow(color: .black.opacity(TNShadow.cardOpacity), radius: TNShadow.cardRadius, y: TNShadow.cardY)
    }
}

#Preview {
    TNRouteCard(
        title: "北岭雾海穿越线",
        meta: "11.4km · 5h48m · 爬升 780m",
        tags: ["山脊线", "日出", "进阶"]
    )
    .padding()
    .background(TNColor.mist100)
}
