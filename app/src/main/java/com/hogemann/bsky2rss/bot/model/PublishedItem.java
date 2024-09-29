package com.hogemann.bsky2rss.bot.model;

import java.util.UUID;

public record PublishedItem(
        UUID sourceId,
        String title,
        String url) { }
