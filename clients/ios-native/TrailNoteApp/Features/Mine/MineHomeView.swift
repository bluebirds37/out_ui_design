import SwiftUI

struct MineHomeView: View {
    var onLogout: () -> Void = {}
    @State private var profile: TrailProfileDTO?
    @State private var favorites: [TrailRouteSummaryDTO] = []
    @State private var isLoading = true
    @State private var errorMessage: String?

    private let profileService: any TrailProfileServicing = TrailServiceRegistry.profileService

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: TNSpacing.large) {
                tnHeroHeader(
                    eyebrow: "我的",
                    title: "统一管理个人资料、收藏路线和后续创作草稿。"
                )

                if isLoading {
                    TNStatusCard(
                        title: "个人资料同步中",
                        message: "正在拉取我的资料与收藏概览。"
                    )
                } else if let errorMessage {
                    TNStatusCard(
                        title: "个人页加载失败",
                        message: errorMessage
                    )
                } else {
                    if let profile {
                        VStack(alignment: .leading, spacing: TNSpacing.medium) {
                            TNSectionHeading(
                                title: profile.nickname,
                                subtitle: "\(profile.city) · \(profile.levelLabel)"
                            )

                            Text(profile.bio)
                                .font(TNTypography.body)
                                .foregroundStyle(TNColor.stone600)
                        }
                        .padding(TNSpacing.medium)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.card, style: .continuous))

                        LazyVGrid(
                            columns: [
                                GridItem(.flexible(), spacing: TNSpacing.medium),
                                GridItem(.flexible(), spacing: TNSpacing.medium),
                            ],
                            spacing: TNSpacing.medium
                        ) {
                            TNStatCard(label: "已发布路线", value: "\(profile.publishedRouteCount)")
                            TNStatCard(label: "收藏夹", value: "\(profile.favoriteRouteCount)")
                            TNStatCard(label: "资料状态", value: "已同步")
                            TNStatCard(label: "当前模式", value: "远程联调")
                        }

                        TNSectionHeading(
                            title: "我的收藏",
                            subtitle: "和 H5 / 小程序统一按路线卡片 + 进入详情按钮展示。"
                        )

                        if favorites.isEmpty {
                            TNStatusCard(
                                title: "收藏列表为空",
                                message: "当前账号还没有收藏路线，后续会在这里继续承接真实互动结果。"
                            )
                        } else {
                            ForEach(Array(favorites.enumerated()), id: \.element.id) { index, route in
                                VStack(alignment: .leading, spacing: TNSpacing.medium) {
                                    NavigationLink {
                                        RouteDetailView(routeId: route.id)
                                    } label: {
                                        TNRouteShowcaseCard(
                                            title: route.title,
                                            meta: "\(route.distanceKm.formattedDistance()) · 收藏 \(route.favoriteCount) · 点位 \(route.waypointCount)",
                                            secondaryMeta: "作者 \(route.authorName)",
                                            tags: route.tags,
                                            accent: index.isMultiple(of: 2)
                                                ? [TNColor.pine900, TNColor.pine500]
                                                : [TNColor.ink800, TNColor.lake500]
                                        )
                                    }
                                    .buttonStyle(.plain)

                                    NavigationLink {
                                        RouteDetailView(routeId: route.id)
                                    } label: {
                                        TNPrimaryButton(title: "进入收藏路线详情", systemImage: "map.fill") {}
                                    }
                                    .buttonStyle(.plain)
                                }
                            }
                        }
                    }
                }

                TNPrimaryButton(title: "编辑个人资料", systemImage: "square.and.pencil") {}
                Button("退出当前账号", role: .destructive) {
                    onLogout()
                }
                .font(TNTypography.body.weight(.semibold))
            }
            .padding(TNSpacing.large)
        }
        .background(TNColor.mist100.ignoresSafeArea())
        .navigationTitle("我的")
        .task {
            await loadProfile()
        }
    }

    @MainActor
    private func loadProfile() async {
        isLoading = true
        errorMessage = nil
        do {
            async let profileTask = profileService.myProfile()
            async let favoritesTask = profileService.myFavorites(page: 1, pageSize: 3)
            profile = try await profileTask
            favorites = try await favoritesTask.records
        } catch {
            profile = nil
            favorites = []
            errorMessage = error.localizedDescription
        }
        isLoading = false
    }
}

#Preview {
    NavigationStack {
        MineHomeView()
    }
}
