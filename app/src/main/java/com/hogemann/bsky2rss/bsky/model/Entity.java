package com.hogemann.bsky2rss.bsky.model;

public record Entity(
        TextSlice index,
        String type,
        String value
) {
}
