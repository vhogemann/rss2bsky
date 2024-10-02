package com.hogemann.bsky2rss.bot.model;

public record PublishedItem(
        String sourceId,
        String title,
        String url) { }
