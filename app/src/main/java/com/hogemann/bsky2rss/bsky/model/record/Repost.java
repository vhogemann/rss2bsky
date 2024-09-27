package com.hogemann.bsky2rss.bsky.model.record;

import com.hogemann.bsky2rss.bsky.model.StrongRef;

import java.time.Instant;

public record Repost(
        StrongRef subject,
        Instant createdAt
) implements Record { }
