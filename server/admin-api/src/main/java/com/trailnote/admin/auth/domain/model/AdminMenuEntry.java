package com.trailnote.admin.auth.domain.model;

public record AdminMenuEntry(
    Long id,
    Long parentId,
    String routeName,
    String routePath,
    String component,
    String redirect,
    String title,
    String icon,
    Integer menuOrder,
    Boolean affixTab
) {
}
