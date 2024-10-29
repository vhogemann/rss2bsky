package com.hogemann.bsky2rss.bsky.model.actor;

import java.time.Instant;

public record ProfileView (
        String did,
        String handle,
        String displayName,
        String description,
        String avatar,
        // ProfileAssociated associated,
        Instant indexedAt,
        Instant createdAt
        // ViewerState viewer,
        // List<Label> labels
) {
}
