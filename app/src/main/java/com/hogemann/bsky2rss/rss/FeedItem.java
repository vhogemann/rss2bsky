package com.hogemann.bsky2rss.rss;

public record FeedItem(
        String title,
        String description,
        String link) {
}
