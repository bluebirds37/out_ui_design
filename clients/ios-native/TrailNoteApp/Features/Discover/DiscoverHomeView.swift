import SwiftUI

struct DiscoverHomeView: View {
    @State private var featuredRoutes: [TrailRouteSummaryDTO] = []
    @State private var routeList: [TrailRouteSummaryDTO] = []
    @State private var isLoading = true
    @State private var errorMessage: String?

    private let routeService: any TrailRouteServicing = TrailServiceRegistry.routeService

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: TNSpacing.large) {
                TNHeroHeader(
                    eyebrow: "精选路线",
                    title: "把山脊线、湖边营地和真实点位装进同一条路线里。",
                    message: "发现页优先承接统一的路线卡片、详情入口和远程错误反馈，和 Android、H5、小程序保持同一信息顺序。"
                )

                if isLoading {
                    TNStatusCard(
                        title: "精选路线同步中",
                        message: "正在拉取发现页真实数据。"
                    )
                } else if let errorMessage {
                    TNStatusCard(
                        title: "发现页加载失败",
                        message: errorMessage
                    )
                } else {
                    if let featuredRoute = featuredRoutes.first {
                        NavigationLink {
                            RouteDetailView(routeId: featuredRoute.id)
                        } label: {
                            TNRouteShowcaseCard(
                                title: featuredRoute.title,
                                meta: "\(featuredRoute.distanceKm.formattedDistance()) · \(featuredRoute.durationMinutes.formattedDuration()) · 爬升 \(featuredRoute.ascentM)m",
                                secondaryMeta: "推荐路线 · 关键点位 \(featuredRoute.waypointCount)",
                                tags: featuredRoute.tags,
                                accent: [TNColor.pine900, TNColor.pine500]
                            )
                        }
                        .buttonStyle(.plain)

                        NavigationLink {
                            RouteDetailView(routeId: featuredRoute.id)
                        } label: {
                            TNPrimaryButton(title: "查看路线详情", systemImage: "arrow.up.right.circle.fill") {}
                        }
                        .buttonStyle(.plain)
                    }

                    TNSectionHeading(title: "继续浏览", subtitle: "保持和 H5 / 小程序一致的路线信息结构与按钮层级。")

                    ForEach(Array(routeList.enumerated()), id: \.element.id) { index, route in
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
                                TNPrimaryButton(title: "进入互动详情", systemImage: "bubble.left.and.bubble.right.fill") {}
                            }
                            .buttonStyle(.plain)
                        }
                    }
                }
            }
            .padding(TNSpacing.large)
        }
        .background(TNColor.mist100.ignoresSafeArea())
        .navigationTitle("发现")
        .task {
            await loadRoutes()
        }
    }

    @MainActor
    private func loadRoutes() async {
        isLoading = true
        errorMessage = nil
        do {
            featuredRoutes = try await routeService.featuredRoutes()
            routeList = try await routeService.routes(page: 1, pageSize: 6).records
        } catch {
            featuredRoutes = []
            routeList = []
            errorMessage = error.localizedDescription
        }
        isLoading = false
    }
}

#Preview {
    NavigationStack {
        DiscoverHomeView()
    }
}
