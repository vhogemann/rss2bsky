package com.hogemann.bsky2rss.bsky.model.notification;

import java.time.Instant;
import java.util.List;

public record ListNotificationsResponse(
        String cursor,
        List<Notification> notifications,
        boolean priority,
        Instant seenAt
) {
}
