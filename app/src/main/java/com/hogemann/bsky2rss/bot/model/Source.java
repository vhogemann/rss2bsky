package com.hogemann.bsky2rss.bot.model;

public record Source(
        int id,
        String feedId,
        String name,
        String rssUrl,
        FeedExtractor feedExtractor,
        String bskyIdentity,
        String bskyPassword
) { }
