import SwiftUI

struct TrailNoteRootView: View {
    @State private var selectedTab: TrailNoteTab = .discover
    @StateObject private var sessionStore = TrailSessionStore()

    var body: some View {
        Group {
            if sessionStore.isAuthenticated {
                TabView(selection: $selectedTab) {
                    NavigationStack {
                        DiscoverHomeView()
                    }
                    .tabItem {
                        Label("发现", systemImage: "mountain.2.fill")
                    }
                    .tag(TrailNoteTab.discover)

                    NavigationStack {
                        SearchHomeView()
                    }
                    .tabItem {
                        Label("搜索", systemImage: "magnifyingglass")
                    }
                    .tag(TrailNoteTab.search)

                    NavigationStack {
                        RecordHomeView()
                    }
                    .tabItem {
                        Label("记录", systemImage: "record.circle.fill")
                    }
                    .tag(TrailNoteTab.record)

                    NavigationStack {
                        MineHomeView(onLogout: {
                            sessionStore.logout()
                        })
                    }
                    .tabItem {
                        Label("我的", systemImage: "person.crop.circle")
                    }
                    .tag(TrailNoteTab.mine)
                }
            } else {
                TrailLoginView(sessionStore: sessionStore)
            }
        }
        .tint(TNColor.pine700)
        .task {
            sessionStore.restoreSession()
        }
        .onReceive(NotificationCenter.default.publisher(for: .trailSessionExpired)) { _ in
            sessionStore.handleSessionExpired()
        }
    }
}

#Preview {
    TrailNoteRootView()
}

@MainActor
final class TrailSessionStore: ObservableObject {
    @Published var isAuthenticated = false
    @Published var isLoggingIn = false
    @Published var loginError = ""

    private let authService: any TrailAuthServicing
    private let defaults: UserDefaults

    init(
        authService: any TrailAuthServicing = TrailServiceRegistry.authService,
        defaults: UserDefaults = .standard
    ) {
        self.authService = authService
        self.defaults = defaults
    }

    func restoreSession() {
        let token = defaults.string(forKey: TrailEnvironment.accessTokenStorageKey) ?? ""
        isAuthenticated = !token.isEmpty
    }

    func login(account: String, password: String) async {
        isLoggingIn = true
        loginError = ""

        do {
            let session = try await authService.login(account: account, password: password)
            defaults.set(session.accessToken, forKey: TrailEnvironment.accessTokenStorageKey)
            isAuthenticated = true
        } catch {
            loginError = error.localizedDescription
        }

        isLoggingIn = false
    }

    func logout() {
        defaults.removeObject(forKey: TrailEnvironment.accessTokenStorageKey)
        loginError = ""
        isAuthenticated = false
    }

    func handleSessionExpired() {
        defaults.removeObject(forKey: TrailEnvironment.accessTokenStorageKey)
        loginError = "登录已失效，请重新登录"
        isAuthenticated = false
    }
}

private struct TrailLoginView: View {
    @ObservedObject var sessionStore: TrailSessionStore
    @State private var account = "hiker@trailnote.app"
    @State private var password = "123456"

    var body: some View {
        VStack(spacing: TNSpacing.large) {
            Spacer()

            VStack(alignment: .leading, spacing: TNSpacing.medium) {
                Text("登录 TrailNote")
                    .font(TNTypography.title)
                    .foregroundStyle(TNColor.ink950)

                Text("iOS / Android / H5 当前统一接入真实业务登录，再进入发现、搜索、详情和我的页面。")
                    .font(TNTypography.body)
                    .foregroundStyle(TNColor.stone600)

                VStack(alignment: .leading, spacing: TNSpacing.small) {
                    Text("账号")
                        .font(TNTypography.caption)
                        .foregroundStyle(TNColor.stone600)
                    TextField("邮箱或手机号", text: $account)
                        .textInputAutocapitalization(.never)
                        .autocorrectionDisabled()
                        .padding(.horizontal, TNSpacing.medium)
                        .frame(height: 52)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.card, style: .continuous))
                }

                VStack(alignment: .leading, spacing: TNSpacing.small) {
                    Text("密码")
                        .font(TNTypography.caption)
                        .foregroundStyle(TNColor.stone600)
                    SecureField("请输入密码", text: $password)
                        .padding(.horizontal, TNSpacing.medium)
                        .frame(height: 52)
                        .background(Color.white)
                        .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.card, style: .continuous))
                }

                if !sessionStore.loginError.isEmpty {
                    Text(sessionStore.loginError)
                        .font(TNTypography.caption)
                        .foregroundStyle(TNColor.danger)
                }

                TNPrimaryButton(
                    title: sessionStore.isLoggingIn ? "登录中..." : "登录",
                    systemImage: "arrow.right.circle.fill"
                ) {
                    Task {
                        await sessionStore.login(
                            account: account.trimmingCharacters(in: .whitespacesAndNewlines),
                            password: password
                        )
                    }
                }
                .disabled(sessionStore.isLoggingIn)
                .opacity(sessionStore.isLoggingIn ? 0.7 : 1)
            }
            .padding(TNSpacing.large)
            .background(TNColor.mist100)
            .clipShape(RoundedRectangle(cornerRadius: TNCornerRadius.sheet, style: .continuous))
            .shadow(color: .black.opacity(TNShadow.cardOpacity), radius: TNShadow.cardRadius, y: TNShadow.cardY)

            Spacer()
        }
        .padding(TNSpacing.large)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(TNColor.mist100.ignoresSafeArea())
    }
}
