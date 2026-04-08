package com.trailnote.common.health;

import java.time.OffsetDateTime;

public record ServiceStatus(
    String service,
    String environment,
    String version,
    OffsetDateTime timestamp
) {
}
