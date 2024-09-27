package com.hogemann.bsky2rss.bsky.model;

public record AuthRequest(
        String identifier,
        String password
) { }
