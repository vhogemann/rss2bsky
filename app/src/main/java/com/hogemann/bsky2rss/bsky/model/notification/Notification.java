package com.hogemann.bsky2rss.bsky.model.notification;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record Notification(
        String uri,
        String cid,
        //Profile.View author,
        Reason reason,
        String reasonSubject,
        // Unknown record
        boolean isRead,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        Instant indexedAt
        // labels
) {
}
