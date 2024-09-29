package com.hogemann.bsky2rss.bot.model;

import java.util.UUID;

public record Source(
        int id,
        UUID feedId,
        String name,
        String rssUrl,
        FeedExtractor feedExtractor,
        String bskyIdentity,
        String bskyPassword
) { }
