package com.hogemann.bsky2rss.rss;

import java.util.Optional;

public record FeedItem(
        String title,
        String description,
        String link,
        Optional<String> thumbnail) {
}
