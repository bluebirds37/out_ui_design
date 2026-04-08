import SwiftUI

struct SearchHomeView: View {
    @State private var searchResult = TrailSearchResultDTO(keyword: "山脊", routes: [])
    @State private var isLoading = true
    @State private var errorMessage: String?

    private let routeService: any TrailRouteServicing = TrailServiceRegistry.routeService

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: TNSpacing.large) {
                tnHeroHeader(
                    eyebrow: "搜索路线",
                    title: "按目的地、作者和点位类型快速定位适合这次出发的路线。"
                )

                if isLoading {
                    TNStatusCard(
                        title: "搜索结果同步中",
                        message: "正在根据关键词拉取真实路线数据。"
                    )
                } else if let errorMessage {
                    TNStatusCard(
                        title: "搜索失败",
                        message: errorMessage
                    )
                } else {
                    Text("当前关键词：\(searchResult.keyword)")
                        .font(TNTypography.body)
                        .foregroundStyle(TNColor.stone600)

                    ForEach(searchResult.routes) { route in
                        NavigationLink {
                            RouteDetailView(routeId: route.id)
                        } label: {
                            TNRouteCard(
                                title: route.title,
                                meta: "\(route.authorName) · \(route.distanceKm.formattedDistance()) · \(route.difficulty.formattedDifficulty())",
                                tags: route.tags
                            )
                        }
                        .buttonStyle(.plain)
                    }
                }
            }
            .padding(TNSpacing.large)
        }
        .background(TNColor.mist100.ignoresSafeArea())
        .navigationTitle("搜索")
        .task {
            await loadSearchResult()
        }
    }

    @MainActor
    private func loadSearchResult() async {
        isLoading = true
        errorMessage = nil
        do {
            searchResult = try await routeService.search(keyword: "山脊")
        } catch {
            searchResult = TrailSearchResultDTO(keyword: "山脊", routes: [])
            errorMessage = error.localizedDescription
        }
        isLoading = false
    }
}

#Preview {
    NavigationStack {
        SearchHomeView()
    }
}
