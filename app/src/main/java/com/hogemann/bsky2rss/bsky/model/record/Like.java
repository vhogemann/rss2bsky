package com.hogemann.bsky2rss.bsky.model.record;

import com.hogemann.bsky2rss.bsky.model.StrongRef;

public record Like(StrongRef subject, String createdAt) implements Record { }
