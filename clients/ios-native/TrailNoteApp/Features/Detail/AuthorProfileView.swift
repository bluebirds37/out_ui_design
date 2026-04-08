import SwiftUI

struct AuthorProfileView: View {
    let authorId: Int

    @State private var author: TrailAuthorProfileDTO?
    @State private var loading = true
    @State private var errorMessage: String?

    private let authorService: any TrailAuthorServicing = TrailServiceRegistry.authorService

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: TNSpacing.large) {
                if loading {
                    TNStatusCard(title: "作者资料同步中", message: "正在加载作者资料与其公开路线。")
                } else if let errorMessage {
                    TNStatusCard(title: "作者主页加载失败", message: errorMessage)
                } else if let author {
                    TNHeroHeader(
                        eyebrow: "作者主页",
                        title: author.nickname,
                        message: "\(author.city ?? "未填写城市") · \(author.levelLabel ?? "等级未设定")"
                    )

                    VStack(alignment: .leading, spacing: TNSpacing.medium) {
                        Text(author.bio ?? "作者暂未补充简介。")
                            .font(TNTypography.body)
                            .foregroundStyle(TNColor.stone600)

                        LazyVGrid(
                            columns: [
                                GridItem(.flexible(), spacing: TNSpacing.medium),
                                GridItem(.flexible(), spacing: TNSpacing.medium),
                            ],
                            spacing: TNSpacing.medium
                        ) {
                            TNStatCard(label: "粉丝", value: "\(author.followerCount)")
                            TNStatCard(label: "关注中", value: "\(author.followingCount)")
                            TNStatCard(label: "公开路线", value: "\(author.publishedRouteCount)")
                            TNStatCard(label: "当前关系", value: author.followed ? "已关注" : "未关注")
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(TNSpacing.medium)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.sheet, style: .continuous))

                    TNPrimaryButton(
                        title: author.followed ? "已关注 · 粉丝 \(author.followerCount)" : "关注作者 · 粉丝 \(author.followerCount)",
                        systemImage: author.followed ? "checkmark.circle.fill" : "person.badge.plus"
                    ) {
                        Task { await toggleFollow() }
                    }

                    TNSectionHeading(title: "作者路线", subtitle: "和其他端统一按路线卡片 + 进入详情按钮展示。")

                    ForEach(Array(author.routes.enumerated()), id: \.element.id) { index, route in
                        VStack(alignment: .leading, spacing: TNSpacing.medium) {
                            NavigationLink {
                                RouteDetailView(routeId: route.id)
                            } label: {
                                TNRouteShowcaseCard(
                                    title: route.title,
                                    meta: "\(route.distanceKm.formattedDistance()) · \(route.durationMinutes.formattedDuration()) · 收藏 \(route.favoriteCount)",
                                    secondaryMeta: "作者路线 \(index + 1)",
                                    tags: route.tags,
                                    accent: index.isMultiple(of: 2)
                                        ? [TNColor.ink800, TNColor.lake500]
                                        : [TNColor.pine900, TNColor.pine500]
                                )
                            }
                            .buttonStyle(.plain)

                            NavigationLink {
                                RouteDetailView(routeId: route.id)
                            } label: {
                                TNPrimaryButton(title: "查看路线详情", systemImage: "map.fill") {}
                            }
                            .buttonStyle(.plain)
                        }
                    }
                }
            }
            .padding(TNSpacing.large)
        }
        .background(TNColor.mist100.ignoresSafeArea())
        .navigationTitle("作者主页")
        .task {
            await load()
        }
    }

    @MainActor
    private func load() async {
        loading = true
        errorMessage = nil
        do {
            author = try await authorService.authorProfile(authorId: authorId)
        } catch {
            author = nil
            errorMessage = error.localizedDescription
        }
        loading = false
    }

    @MainActor
    private func toggleFollow() async {
        guard let author else { return }
        do {
            let result = try await authorService.toggleAuthorFollow(authorId: author.id)
            self.author = TrailAuthorProfileDTO(
                id: author.id,
                nickname: author.nickname,
                avatarUrl: author.avatarUrl,
                bio: author.bio,
                city: author.city,
                levelLabel: author.levelLabel,
                followerCount: result.followerCount,
                followingCount: author.followingCount,
                publishedRouteCount: author.publishedRouteCount,
                followed: result.followed,
                routes: author.routes
            )
        } catch {
            errorMessage = error.localizedDescription
        }
    }
}
