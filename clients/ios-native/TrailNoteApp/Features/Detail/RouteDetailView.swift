import SwiftUI

struct RouteDetailView: View {
    let routeId: Int

    @State private var route: TrailRouteDetailDTO?
    @State private var author: TrailAuthorProfileDTO?
    @State private var comments: [TrailRouteCommentDTO] = []
    @State private var commentContent = ""
    @State private var loading = true
    @State private var errorMessage: String?
    @State private var commentsLoading = false
    @State private var commentsErrorMessage: String?
    @State private var submittingComment = false

    private let routeService: any TrailRouteServicing = TrailServiceRegistry.routeService
    private let authorService: any TrailAuthorServicing = TrailServiceRegistry.authorService

    var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: TNSpacing.large) {
                if loading {
                    TNStatusCard(title: "正在加载路线详情", message: "同步点位、作者信息与评论内容。")
                } else if let errorMessage {
                    TNStatusCard(title: "路线详情加载失败", message: errorMessage)
                } else if let route {
                    TNRouteShowcaseCard(
                        title: route.title,
                        meta: "\(route.distanceKm.formattedDistance()) · \(route.durationMinutes.formattedDuration()) · 爬升 \(route.ascentM)m",
                        secondaryMeta: "路线详情 · 点位 \(route.waypoints.count) · 评论 \(route.commentCount)",
                        tags: route.tags,
                        accent: [TNColor.pine900, TNColor.pine500]
                    )

                    HStack(spacing: TNSpacing.small) {
                        TNPrimaryButton(
                            title: route.favorited ? "已收藏 · \(route.favoriteCount)" : "收藏 · \(route.favoriteCount)",
                            systemImage: route.favorited ? "heart.fill" : "heart"
                        ) {
                            Task { await toggleFavorite() }
                        }

                        if let author {
                            NavigationLink {
                                AuthorProfileView(authorId: author.id)
                            } label: {
                                TNPrimaryButton(
                                    title: author.followed ? "已关注作者" : "关注作者",
                                    systemImage: author.followed ? "checkmark.circle.fill" : "person.badge.plus"
                                ) {}
                            }
                            .frame(maxWidth: .infinity)
                        }
                    }

                    VStack(alignment: .leading, spacing: TNSpacing.small) {
                        TNSectionHeading(title: "路线说明", subtitle: "和其他端统一把说明、作者入口与统计信息放在头图区之后。")
                        Text(route.description)
                            .font(TNTypography.body)
                            .foregroundStyle(TNColor.stone600)
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(TNSpacing.medium)
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.sheet, style: .continuous))

                    if let author {
                        NavigationLink {
                            AuthorProfileView(authorId: author.id)
                        } label: {
                            VStack(alignment: .leading, spacing: TNSpacing.medium) {
                                TNSectionHeading(title: "\(author.nickname) · \(author.city ?? "城市未填写")", subtitle: "作者信息")
                                LazyVGrid(
                                    columns: [
                                        GridItem(.flexible(), spacing: TNSpacing.medium),
                                        GridItem(.flexible(), spacing: TNSpacing.medium),
                                    ],
                                    spacing: TNSpacing.medium
                                ) {
                                    TNStatCard(label: "粉丝", value: "\(author.followerCount)")
                                    TNStatCard(label: "已发布", value: "\(author.publishedRouteCount)")
                                }
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(TNSpacing.medium)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.sheet, style: .continuous))
                        }
                        .buttonStyle(.plain)
                    }

                    VStack(alignment: .leading, spacing: TNSpacing.small) {
                        TNSectionHeading(title: "关键点位", subtitle: "统一按点位卡片列表展示类型、海拔和媒体数量。")

                        ForEach(route.waypoints) { waypoint in
                            VStack(alignment: .leading, spacing: TNSpacing.xSmall) {
                                Text(waypoint.title)
                                    .font(TNTypography.title)
                                    .foregroundStyle(TNColor.ink950)
                                Text("\(waypoint.type) · 海拔 \(waypoint.altitudeM ?? 0)m · 媒体 \(waypoint.mediaList.count)")
                                    .font(TNTypography.caption)
                                    .foregroundStyle(TNColor.stone600)
                                Text(waypoint.description)
                                    .font(TNTypography.body)
                                    .foregroundStyle(TNColor.stone600)
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(TNSpacing.medium)
                            .background(Color.white)
                            .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.sheet, style: .continuous))
                        }
                    }

                    VStack(alignment: .leading, spacing: TNSpacing.small) {
                        TNSectionHeading(title: "路线评论", subtitle: "输入区、提交按钮和最新评论保持同一层级。")

                        TextField("写下你的路线反馈", text: $commentContent, axis: .vertical)
                            .textFieldStyle(.roundedBorder)

                        TNPrimaryButton(title: submittingComment ? "发送中..." : "发布评论", systemImage: "paperplane.fill") {
                            Task { await submitComment() }
                        }

                        if commentsLoading {
                            TNStatusCard(title: "评论同步中", message: "正在加载当前路线评论。")
                        } else if let commentsErrorMessage {
                            TNStatusCard(title: "评论加载失败", message: commentsErrorMessage)
                        } else {
                            ForEach(comments) { comment in
                                VStack(alignment: .leading, spacing: TNSpacing.xSmall) {
                                    HStack {
                                        Text(comment.authorName)
                                            .font(TNTypography.title)
                                        Spacer()
                                        Text(comment.createdAt.formattedDateText())
                                            .font(TNTypography.caption)
                                            .foregroundStyle(TNColor.stone600)
                                    }
                                    Text(comment.content)
                                        .font(TNTypography.body)
                                        .foregroundStyle(TNColor.stone600)
                                }
                                .frame(maxWidth: .infinity, alignment: .leading)
                                .padding(TNSpacing.medium)
                                .background(Color.white)
                                .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.sheet, style: .continuous))
                            }
                        }
                    }
                }
            }
            .padding(TNSpacing.large)
        }
        .background(TNColor.mist100.ignoresSafeArea())
        .navigationTitle("路线详情")
        .task {
            await load()
        }
    }

    @MainActor
    private func load() async {
        loading = true
        errorMessage = nil
        do {
            let route = try await routeService.routeDetail(routeId: routeId)
            let author = try await authorService.authorProfile(authorId: route.authorId)
            self.route = route
            self.author = author
            await loadComments()
        } catch {
            self.route = nil
            self.author = nil
            comments = []
            errorMessage = error.localizedDescription
        }
        loading = false
    }

    @MainActor
    private func loadComments() async {
        commentsLoading = true
        commentsErrorMessage = nil
        do {
            comments = try await routeService.routeComments(routeId: routeId, page: 1, pageSize: 20).records
        } catch {
            comments = []
            commentsErrorMessage = error.localizedDescription
        }
        commentsLoading = false
    }

    @MainActor
    private func toggleFavorite() async {
        guard let route else { return }
        do {
            let result = try await routeService.toggleRouteFavorite(routeId: route.id)
            self.route = TrailRouteDetailDTO(
                id: route.id,
                title: route.title,
                authorName: route.authorName,
                authorId: route.authorId,
                description: route.description,
                difficulty: route.difficulty,
                distanceKm: route.distanceKm,
                durationMinutes: route.durationMinutes,
                ascentM: route.ascentM,
                maxAltitudeM: route.maxAltitudeM,
                favoriteCount: result.favoriteCount,
                commentCount: route.commentCount,
                favorited: result.favorited,
                tags: route.tags,
                waypoints: route.waypoints
            )
        } catch {
            errorMessage = error.localizedDescription
        }
    }

    @MainActor
    private func submitComment() async {
        guard let route else { return }
        let content = commentContent.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !content.isEmpty else { return }
        submittingComment = true
        do {
            let result = try await routeService.addRouteComment(routeId: route.id, content: content)
            commentContent = ""
            self.route = TrailRouteDetailDTO(
                id: route.id,
                title: route.title,
                authorName: route.authorName,
                authorId: route.authorId,
                description: route.description,
                difficulty: route.difficulty,
                distanceKm: route.distanceKm,
                durationMinutes: route.durationMinutes,
                ascentM: route.ascentM,
                maxAltitudeM: route.maxAltitudeM,
                favoriteCount: route.favoriteCount,
                commentCount: result.commentCount,
                favorited: route.favorited,
                tags: route.tags,
                waypoints: route.waypoints
            )
            await loadComments()
        } catch {
            commentsErrorMessage = error.localizedDescription
        }
        submittingComment = false
    }
}
