package com.hogemann.bsky2rss.bsky.model;

public record AuthResponse(
        String accessJwt,
        String refreshJwt,
        String did ) { }
