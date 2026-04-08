package com.trailnote.api.creator.application;

import com.trailnote.api.creator.application.view.CreatorRouteDraftView;
import com.trailnote.api.creator.application.view.CreatorRouteDraftViewMapper;
import com.trailnote.api.creator.domain.model.CreatorRouteRow;
import com.trailnote.api.creator.domain.model.CreatorRouteDraft;
import com.trailnote.api.creator.domain.model.CreatorRouteDraftSnapshot;
import com.trailnote.api.creator.domain.model.PublishRouteResult;
import com.trailnote.api.creator.domain.model.SaveDraftCommand;
import com.trailnote.api.creator.domain.repository.CreatorRouteRepository;
import com.trailnote.api.route.domain.model.RouteLifecycleStatus;
import com.trailnote.api.support.DemoCurrentUserProvider;
import com.trailnote.common.api.PageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatorRouteService {

  private final CreatorRouteRepository repository;
  private final CreatorRouteDraftViewMapper viewMapper;
  private final DemoCurrentUserProvider currentUserProvider;

  public CreatorRouteService(
      CreatorRouteRepository repository,
      CreatorRouteDraftViewMapper viewMapper,
      DemoCurrentUserProvider currentUserProvider
  ) {
    this.repository = repository;
    this.viewMapper = viewMapper;
    this.currentUserProvider = currentUserProvider;
  }

  public PageResponse<CreatorRouteRow> pageMyRoutes(long page, long pageSize) {
    return repository.pageMyRoutes(currentUserProvider.getCurrentUserId(), page, pageSize);
  }

  public CreatorRouteDraftView getCurrentDraft() {
    CreatorRouteDraftSnapshot snapshot = repository.getCurrentDraft(currentUserProvider.getCurrentUserId());
    return viewMapper.toView(snapshot);
  }

  public CreatorRouteDraftView getDraftDetail(Long routeId) {
    return viewMapper.toView(repository.getDraftDetail(currentUserProvider.getCurrentUserId(), routeId));
  }

  @Transactional
  public CreatorRouteDraftView saveDraft(SaveDraftCommand command) {
    CreatorRouteDraft draft = CreatorRouteDraft.from(command);
    draft.validateForSave();
    return viewMapper.toView(repository.saveDraft(currentUserProvider.getCurrentUserId(), draft));
  }

  @Transactional
  public PublishRouteResult publishDraft(Long routeId) {
    CreatorRouteDraftSnapshot draft = repository.getDraftDetail(currentUserProvider.getCurrentUserId(), routeId);
    RouteLifecycleStatus nextStatus =
        RouteLifecycleStatus.fromValue(CreatorRouteDraft.from(draft).resolveNextPublishStatus());
    if (nextStatus.value().equals(draft.status())) {
      return new PublishRouteResult(draft.id(), draft.status(), draft.updatedAt());
    }
    return repository.updateStatus(currentUserProvider.getCurrentUserId(), routeId, nextStatus);
  }
}
