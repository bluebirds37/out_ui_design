package com.trailnote.android.core

enum class TrailViewState {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR,
}

enum class TrailRecordFlowState {
    IDLE,
    RECORDING,
    PAUSED,
    ENDED,
}

enum class TrailRouteDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
}

enum class TrailPublishStatus {
    DRAFT,
    PENDING_REVIEW,
    PUBLISHED,
    REJECTED,
    ARCHIVED,
}

enum class TrailInteractionAction {
    TOGGLE_FAVORITE,
    TOGGLE_FOLLOW,
    SAVE_DRAFT,
    SUBMIT_FOR_REVIEW,
}
