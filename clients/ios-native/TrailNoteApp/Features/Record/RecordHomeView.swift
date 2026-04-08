import SwiftUI

struct RecordHomeView: View {
    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: TNSpacing.large) {
                tnHeroHeader(
                    eyebrow: "记录准备",
                    title: "尽量少打断地记录轨迹、点位与现场信息。"
                )

                VStack(alignment: .leading, spacing: TNSpacing.small) {
                    Text("当前状态")
                        .font(TNTypography.caption)
                        .foregroundStyle(TNColor.stone600)
                    Text("未开始")
                        .font(TNTypography.title)
                        .foregroundStyle(TNColor.ink950)
                    Text("离线草稿、点位媒体和轨迹状态将继续在后续阶段接入。")
                        .font(TNTypography.body)
                        .foregroundStyle(TNColor.stone600)
                }
                .padding(TNSpacing.medium)
                .frame(maxWidth: .infinity, alignment: .leading)
                .background(Color.white)
                .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.card, style: .continuous))

                TNPrimaryButton(title: "开始记录", systemImage: "location.fill") {}
            }
            .padding(TNSpacing.large)
        }
        .background(TNColor.mist100.ignoresSafeArea())
        .navigationTitle("记录")
    }
}

#Preview {
    NavigationStack {
        RecordHomeView()
    }
}
