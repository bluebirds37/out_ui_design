package com.trailnote.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.trailnote.api.auth.interfaces.http.AuthController;
import com.trailnote.api.support.interfaces.http.PublicHealthController;
import com.trailnote.api.creator.application.view.CreatorRouteDraftMediaView;
import com.trailnote.api.creator.application.view.CreatorRouteDraftView;
import com.trailnote.api.creator.application.view.CreatorRouteDraftWaypointView;
import com.trailnote.api.creator.interfaces.http.CreatorRouteController;
import com.trailnote.api.creator.domain.model.CreatorRouteRow;
import com.trailnote.api.creator.domain.model.PublishRouteResult;
import com.trailnote.api.creator.domain.model.RouteDraftMediaInput;
import com.trailnote.api.creator.domain.model.RouteDraftWaypointInput;
import com.trailnote.api.creator.domain.model.SaveDraftCommand;
import com.trailnote.api.creator.application.CreatorRouteService;
import com.trailnote.api.interaction.interfaces.http.RouteInteractionController;
import com.trailnote.api.interaction.domain.model.CommentCreateResult;
import com.trailnote.api.interaction.domain.model.FavoriteToggleResult;
import com.trailnote.api.interaction.domain.model.RouteCommentItem;
import com.trailnote.api.interaction.application.RouteCommentService;
import com.trailnote.api.interaction.application.RouteFavoriteService;
import com.trailnote.api.route.interfaces.http.RouteController;
import com.trailnote.api.route.domain.model.MediaType;
import com.trailnote.api.route.domain.model.RouteDetail;
import com.trailnote.api.route.domain.model.RouteDifficulty;
import com.trailnote.api.route.domain.model.RouteSummary;
import com.trailnote.api.route.domain.model.WaypointSummary;
import com.trailnote.api.route.domain.model.WaypointType;
import com.trailnote.api.route.application.RouteQueryService;
import com.trailnote.api.search.interfaces.http.SearchController;
import com.trailnote.api.search.domain.model.MapRouteItem;
import com.trailnote.api.search.domain.model.SearchAuthorItem;
import com.trailnote.api.search.domain.model.SearchResult;
import com.trailnote.api.search.domain.model.SearchWaypointItem;
import com.trailnote.api.search.application.SearchService;
import com.trailnote.api.social.interfaces.http.AuthorController;
import com.trailnote.api.social.domain.model.AuthorProfile;
import com.trailnote.api.social.domain.model.FollowToggleResult;
import com.trailnote.api.social.application.AuthorService;
import com.trailnote.api.user.interfaces.http.ProfileController;
import com.trailnote.api.user.domain.model.MyProfile;
import com.trailnote.api.user.domain.model.UpdateProfileCommand;
import com.trailnote.api.user.application.ProfileService;
import com.trailnote.common.api.ApiResponse;
import com.trailnote.common.api.PageResponse;
import com.trailnote.common.health.ServiceStatus;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ApiControllerCharacterizationTests {

  @Test
  void publicHealthControllerReturnsConfiguredServiceStatus() {
    PublicHealthController controller = new PublicHealthController();
    ReflectionTestUtils.setField(controller, "applicationName", "trailnote-api");
    ReflectionTestUtils.setField(controller, "environment", "test");
    ReflectionTestUtils.setField(controller, "version", "1.2.3");

    ApiResponse<ServiceStatus> response = controller.health();

    assertEquals("trailnote-api", response.data().service());
    assertEquals("test", response.data().environment());
    assertEquals("1.2.3", response.data().version());
    assertNotNull(response.data().timestamp());
  }

  @Test
  void authControllerReturnsBootstrapLoginResponse() {
    AuthController controller = new AuthController();

    ApiResponse<AuthController.LoginResponse> response =
        controller.login(new AuthController.LoginRequest("runner@example.com", "secret1"));

    assertEquals("mock-token-for-bootstrap", response.data().accessToken());
    assertEquals("TrailNote User", response.data().nickname());
  }

  @Test
  void routeControllerDelegatesFeaturedPageAndDetailQueries() {
    StubRouteQueryService routeQueryService = new StubRouteQueryService();
    RouteController controller = new RouteController(routeQueryService);
    List<RouteSummary> featured = List.of(routeSummary(11L, "Featured Trail"));
    PageResponse<RouteSummary> pageResponse = PageResponse.of(featured, 1, 2, 5);
    RouteDetail detail = routeDetail(11L, "Featured Trail");
    routeQueryService.featuredRoutes = featured;
    routeQueryService.pageResponse = pageResponse;
    routeQueryService.routeDetail = detail;

    assertEquals(featured, controller.featured().data());
    assertEquals(pageResponse, controller.page(2, 5).data());
    assertEquals(detail, controller.detail(11L).data());
    assertEquals(2, routeQueryService.lastPage);
    assertEquals(5, routeQueryService.lastPageSize);
    assertEquals(11L, routeQueryService.lastRouteId);
  }

  @Test
  void creatorRouteControllerMapsSaveDraftRequestIntoCommand() {
    StubCreatorRouteService creatorRouteService = new StubCreatorRouteService();
    CreatorRouteController controller = new CreatorRouteController(creatorRouteService);
    CreatorRouteController.SaveDraftMediaRequest mediaRequest =
        new CreatorRouteController.SaveDraftMediaRequest(7L, MediaType.PHOTO, "cover.jpg", "media.jpg", 12);
    CreatorRouteController.SaveDraftWaypointRequest waypointRequest =
        new CreatorRouteController.SaveDraftWaypointRequest(
            5L,
            "观景台",
            WaypointType.VIEWPOINT,
            "风景很好",
            31.2,
            121.5,
            123,
            1,
            List.of(mediaRequest)
        );
    CreatorRouteController.SaveDraftRequest request =
        new CreatorRouteController.SaveDraftRequest(
            3L,
            "环湖路线",
            "cover.png",
            "适合周末轻徒步",
            RouteDifficulty.INTERMEDIATE,
            8.6,
            95,
            280,
            520,
            List.of("湖景", "周末"),
            List.of(waypointRequest)
        );
    CreatorRouteDraftView detail = routeDraftView(3L);
    creatorRouteService.savedDraftResult = detail;

    ApiResponse<CreatorRouteDraftView> response = controller.saveDraft(request);

    assertEquals(3L, creatorRouteService.savedCommand.routeId());
    assertEquals("环湖路线", creatorRouteService.savedCommand.title());
    assertEquals(RouteDifficulty.INTERMEDIATE, creatorRouteService.savedCommand.difficulty());
    assertEquals(List.of("湖景", "周末"), creatorRouteService.savedCommand.tags());
    assertEquals(1, creatorRouteService.savedCommand.waypoints().size());
    assertEquals("观景台", creatorRouteService.savedCommand.waypoints().get(0).title());
    assertEquals(MediaType.PHOTO, creatorRouteService.savedCommand.waypoints().get(0).mediaList().get(0).mediaType());
    assertEquals(detail, response.data());
  }

  @Test
  void creatorRouteControllerDelegatesOtherReadAndPublishCalls() {
    StubCreatorRouteService creatorRouteService = new StubCreatorRouteService();
    CreatorRouteController controller = new CreatorRouteController(creatorRouteService);
    PageResponse<CreatorRouteRow> pageResponse =
        PageResponse.of(List.of(creatorRouteRow(9L)), 1, 1, 10);
    CreatorRouteDraftView currentDraft = routeDraftView(9L);
    PublishRouteResult publishResult = new PublishRouteResult(9L, "PENDING_REVIEW", LocalDateTime.now());
    creatorRouteService.routePage = pageResponse;
    creatorRouteService.currentDraft = currentDraft;
    creatorRouteService.draftDetail = currentDraft;
    creatorRouteService.publishResult = publishResult;

    assertEquals(pageResponse, controller.myRoutes(1, 10).data());
    assertEquals(currentDraft, controller.currentDraft().data());
    assertEquals(currentDraft, controller.draftDetail(9L).data());
    assertEquals(publishResult, controller.publish(9L).data());
  }

  @Test
  void routeInteractionControllerDelegatesFavoriteAndComments() {
    StubRouteFavoriteService routeFavoriteService = new StubRouteFavoriteService();
    StubRouteCommentService routeCommentService = new StubRouteCommentService();
    RouteInteractionController controller =
        new RouteInteractionController(routeFavoriteService, routeCommentService);
    FavoriteToggleResult favoriteResult = new FavoriteToggleResult(true, 12);
    PageResponse<RouteCommentItem> pageResponse =
        PageResponse.of(List.of(commentItem(9L, "很棒")), 1, 1, 20);
    CommentCreateResult createResult = new CommentCreateResult(commentItem(10L, "收藏了"), 3);
    routeFavoriteService.favoriteToggleResult = favoriteResult;
    routeCommentService.commentPage = pageResponse;
    routeCommentService.commentCreateResult = createResult;

    assertEquals(favoriteResult, controller.toggleFavorite(5L).data());
    assertEquals(pageResponse, controller.pageComments(5L, 1, 20).data());
    assertEquals(
        createResult,
        controller.addComment(5L, new RouteInteractionController.CreateCommentRequest("收藏了")).data()
    );
    assertEquals(5L, routeFavoriteService.lastRouteId);
    assertEquals(5L, routeCommentService.lastPageRouteId);
    assertEquals("收藏了", routeCommentService.lastCommentContent);
  }

  @Test
  void searchControllerDelegatesSearchQueries() {
    StubSearchService searchService = new StubSearchService();
    SearchController controller = new SearchController(searchService);
    SearchResult searchResult = new SearchResult(
        "湖",
        List.of(routeSummary(3L, "湖畔步道")),
        List.of(new SearchAuthorItem(1L, "A", "a.png", "bio", "Shanghai", "Lv1", 3, true)),
        List.of(new SearchWaypointItem(5L, 3L, "湖畔步道", "观景点", "VIEWPOINT", "desc", 30.0, 120.0))
    );
    List<MapRouteItem> mapItems = List.of(
        new MapRouteItem(3L, "湖畔步道", "cover.png", "Alice", RouteDifficulty.BEGINNER, 3.2, 40, 6, 30.0, 120.0, List.of("湖景"))
    );
    searchService.searchResult = searchResult;
    searchService.mapItems = mapItems;

    assertEquals(searchResult, controller.search("湖").data());
    assertEquals(mapItems, controller.mapRoutes("湖").data());
    assertEquals("湖", searchService.lastSearchKeyword);
    assertEquals("湖", searchService.lastMapKeyword);
  }

  @Test
  void authorControllerDelegatesProfileAndFollow() {
    StubAuthorService authorService = new StubAuthorService();
    AuthorController controller = new AuthorController(authorService);
    AuthorProfile profile = new AuthorProfile(
        7L,
        "Alice",
        "avatar.png",
        "bio",
        "Shanghai",
        "Lv3",
        12,
        4,
        8,
        true,
        List.of(routeSummary(7L, "山脊路线"))
    );
    FollowToggleResult toggleResult = new FollowToggleResult(true, 13);
    authorService.authorProfile = profile;
    authorService.followToggleResult = toggleResult;

    assertEquals(profile, controller.profile(7L).data());
    assertEquals(toggleResult, controller.toggleFollow(7L).data());
    assertEquals(7L, authorService.lastProfileAuthorId);
    assertEquals(7L, authorService.lastFollowAuthorId);
  }

  @Test
  void profileControllerDelegatesProfileUpdateAndFavorites() {
    StubProfileService profileService = new StubProfileService();
    StubRouteFavoriteService routeFavoriteService = new StubRouteFavoriteService();
    ProfileController controller = new ProfileController(profileService, routeFavoriteService);
    MyProfile profile = new MyProfile(1L, "Runner", "avatar.png", "bio", "Shanghai", "Lv2", 6, 9);
    PageResponse<RouteSummary> favorites = PageResponse.of(List.of(routeSummary(21L, "Favorites")), 1, 1, 10);
    profileService.profile = profile;
    routeFavoriteService.favoritePage = favorites;

    ApiResponse<MyProfile> updateResponse = controller.updateProfile(
        new ProfileController.UpdateProfileRequest("Runner", "avatar.png", "bio", "Shanghai", "Lv2")
    );

    assertEquals(profile, controller.profile().data());
    assertEquals("Runner", profileService.lastUpdateCommand.nickname());
    assertEquals(profile, updateResponse.data());
    assertEquals(favorites, controller.favorites(1, 10).data());
    assertEquals(1, routeFavoriteService.lastFavoritesPage);
    assertEquals(10, routeFavoriteService.lastFavoritesPageSize);
  }

  private static CreatorRouteRow creatorRouteRow(Long id) {
    return new CreatorRouteRow(
        id,
        "环湖路线",
        "cover.png",
        "DRAFT",
        8.6,
        95,
        1,
        3,
        2,
        List.of("湖景"),
        LocalDateTime.of(2026, 4, 1, 10, 0)
    );
  }

  private static CreatorRouteDraftView routeDraftView(Long id) {
    return new CreatorRouteDraftView(
        id,
        "环湖路线",
        "cover.png",
        "适合周末轻徒步",
        RouteDifficulty.INTERMEDIATE,
        8.6,
        95,
        280,
        520,
        "DRAFT",
        List.of("湖景"),
        List.of(new CreatorRouteDraftWaypointView(
            5L,
            "观景台",
            WaypointType.VIEWPOINT,
            "风景很好",
            31.2,
            121.5,
            123,
            1,
            List.of(new CreatorRouteDraftMediaView(
                7L,
                MediaType.PHOTO,
                "cover.jpg",
                "media.jpg",
                12
            ))
        )),
        LocalDateTime.of(2026, 4, 1, 10, 0)
    );
  }

  private static RouteSummary routeSummary(Long id, String title) {
    return new RouteSummary(
        id,
        title,
        "cover.png",
        "Alice",
        1L,
        RouteDifficulty.BEGINNER,
        4.8,
        60,
        120,
        3,
        8,
        List.of("周末")
    );
  }

  private static RouteDetail routeDetail(Long id, String title) {
    return new RouteDetail(
        id,
        title,
        "Alice",
        1L,
        "desc",
        RouteDifficulty.BEGINNER,
        4.8,
        60,
        120,
        230,
        8,
        3,
        true,
        List.of("周末"),
        List.<WaypointSummary>of()
    );
  }

  private static RouteCommentItem commentItem(Long id, String content) {
    return new RouteCommentItem(
        id,
        5L,
        2L,
        "Alice",
        "avatar.png",
        content,
        true,
        LocalDateTime.of(2026, 4, 1, 11, 0)
    );
  }

  private static final class StubRouteQueryService extends RouteQueryService {
    private List<RouteSummary> featuredRoutes = List.of();
    private PageResponse<RouteSummary> pageResponse = PageResponse.of(List.of(), 0, 1, 10);
    private RouteDetail routeDetail = routeDetail(0L, "");
    private long lastPage;
    private long lastPageSize;
    private Long lastRouteId;

    StubRouteQueryService() {
      super(null, null);
    }

    @Override
    public List<RouteSummary> listFeaturedRoutes() {
      return featuredRoutes;
    }

    @Override
    public PageResponse<RouteSummary> page(long page, long pageSize) {
      this.lastPage = page;
      this.lastPageSize = pageSize;
      return pageResponse;
    }

    @Override
    public RouteDetail getRouteDetail(Long routeId) {
      this.lastRouteId = routeId;
      return routeDetail;
    }
  }

  private static final class StubCreatorRouteService extends CreatorRouteService {
    private PageResponse<CreatorRouteRow> routePage = PageResponse.of(List.of(), 0, 1, 10);
    private CreatorRouteDraftView currentDraft;
    private CreatorRouteDraftView draftDetail;
    private PublishRouteResult publishResult;
    private SaveDraftCommand savedCommand;
    private CreatorRouteDraftView savedDraftResult;

    StubCreatorRouteService() {
      super(null, null, null);
    }

    @Override
    public PageResponse<CreatorRouteRow> pageMyRoutes(long page, long pageSize) {
      return routePage;
    }

    @Override
    public CreatorRouteDraftView getCurrentDraft() {
      return currentDraft;
    }

    @Override
    public CreatorRouteDraftView getDraftDetail(Long routeId) {
      return draftDetail;
    }

    @Override
    public CreatorRouteDraftView saveDraft(SaveDraftCommand command) {
      this.savedCommand = command;
      return savedDraftResult;
    }

    @Override
    public PublishRouteResult publishDraft(Long routeId) {
      return publishResult;
    }
  }

  private static final class StubRouteFavoriteService extends RouteFavoriteService {
    private FavoriteToggleResult favoriteToggleResult;
    private PageResponse<RouteSummary> favoritePage = PageResponse.of(List.of(), 0, 1, 10);
    private Long lastRouteId;
    private long lastFavoritesPage;
    private long lastFavoritesPageSize;

    StubRouteFavoriteService() {
      super(null, null);
    }

    @Override
    public FavoriteToggleResult toggleFavorite(Long routeId) {
      this.lastRouteId = routeId;
      return favoriteToggleResult;
    }

    @Override
    public PageResponse<RouteSummary> pageMyFavorites(long page, long pageSize) {
      this.lastFavoritesPage = page;
      this.lastFavoritesPageSize = pageSize;
      return favoritePage;
    }
  }

  private static final class StubRouteCommentService extends RouteCommentService {
    private PageResponse<RouteCommentItem> commentPage = PageResponse.of(List.of(), 0, 1, 20);
    private CommentCreateResult commentCreateResult;
    private Long lastPageRouteId;
    private Long lastAddRouteId;
    private String lastCommentContent;

    StubRouteCommentService() {
      super(null, null);
    }

    @Override
    public PageResponse<RouteCommentItem> pageComments(Long routeId, long page, long pageSize) {
      this.lastPageRouteId = routeId;
      return commentPage;
    }

    @Override
    public CommentCreateResult addComment(Long routeId, String content) {
      this.lastAddRouteId = routeId;
      this.lastCommentContent = content;
      return commentCreateResult;
    }
  }

  private static final class StubSearchService extends SearchService {
    private SearchResult searchResult;
    private List<MapRouteItem> mapItems = List.of();
    private String lastSearchKeyword;
    private String lastMapKeyword;

    StubSearchService() {
      super(null, null);
    }

    @Override
    public SearchResult search(String keyword) {
      this.lastSearchKeyword = keyword;
      return searchResult;
    }

    @Override
    public List<MapRouteItem> mapRoutes(String keyword) {
      this.lastMapKeyword = keyword;
      return mapItems;
    }
  }

  private static final class StubAuthorService extends AuthorService {
    private AuthorProfile authorProfile;
    private FollowToggleResult followToggleResult;
    private Long lastProfileAuthorId;
    private Long lastFollowAuthorId;

    StubAuthorService() {
      super(null, null);
    }

    @Override
    public AuthorProfile getAuthorProfile(Long authorId) {
      this.lastProfileAuthorId = authorId;
      return authorProfile;
    }

    @Override
    public FollowToggleResult toggleFollow(Long authorId) {
      this.lastFollowAuthorId = authorId;
      return followToggleResult;
    }
  }

  private static final class StubProfileService extends ProfileService {
    private MyProfile profile;
    private UpdateProfileCommand lastUpdateCommand;

    StubProfileService() {
      super(null, null);
    }

    @Override
    public MyProfile getMyProfile() {
      return profile;
    }

    @Override
    public MyProfile updateMyProfile(UpdateProfileCommand command) {
      this.lastUpdateCommand = command;
      return profile;
    }
  }
}
