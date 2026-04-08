import SwiftUI

struct TNSectionHeading: View {
    let title: String
    let subtitle: String?

    var body: some View {
        VStack(alignment: .leading, spacing: TNSpacing.xSmall) {
            Text(title)
                .font(TNTypography.title)
                .foregroundStyle(TNColor.ink950)

            if let subtitle {
                Text(subtitle)
                    .font(TNTypography.caption)
                    .foregroundStyle(TNColor.stone600)
            }
        }
    }
}

struct TNStatCard: View {
    let label: String
    let value: String

    var body: some View {
        VStack(alignment: .leading, spacing: TNSpacing.xSmall) {
            Text(label)
                .font(TNTypography.caption)
                .foregroundStyle(TNColor.stone600)

            Text(value)
                .font(.system(size: 24, weight: .bold, design: .rounded))
                .foregroundStyle(TNColor.ink950)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(TNSpacing.medium)
        .background(TNColor.mist100)
        .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.card, style: .continuous))
        .overlay {
            RoundedRectangle(cornerRadius: TNCornerRadius.card, style: .continuous)
                .stroke(TNColor.stone300.opacity(0.35), lineWidth: 1)
        }
    }
}

struct TNRouteShowcaseCard: View {
    let title: String
    let meta: String
    let secondaryMeta: String?
    let tags: [String]
    let accent: [Color]

    var body: some View {
        VStack(alignment: .leading, spacing: TNSpacing.medium) {
            RoundedRectangle(cornerRadius: TNCornerRadius.sheet, style: .continuous)
                .fill(
                    LinearGradient(
                        colors: accent,
                        startPoint: .topLeading,
                        endPoint: .bottomTrailing
                    )
                )
                .frame(height: 180)
                .overlay(alignment: .topLeading) {
                    Text("TRAILNOTE")
                        .font(TNTypography.caption)
                        .foregroundStyle(.white.opacity(0.82))
                        .padding(TNSpacing.medium)
                }

            VStack(alignment: .leading, spacing: TNSpacing.small) {
                Text(title)
                    .font(TNTypography.title)
                    .foregroundStyle(TNColor.ink950)

                Text(meta)
                    .font(TNTypography.body.weight(.medium))
                    .foregroundStyle(TNColor.ink950)

                if let secondaryMeta {
                    Text(secondaryMeta)
                        .font(TNTypography.caption)
                        .foregroundStyle(TNColor.stone600)
                }

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
        .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.sheet, style: .continuous))
        .shadow(color: .black.opacity(TNShadow.cardOpacity), radius: TNShadow.cardRadius, y: TNShadow.cardY)
        .overlay {
            RoundedRectangle(cornerRadius: TNCornerRadius.sheet, style: .continuous)
                .stroke(TNColor.stone300.opacity(0.22), lineWidth: 1)
        }
    }
}
