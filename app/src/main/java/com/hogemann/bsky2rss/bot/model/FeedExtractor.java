package com.hogemann.bsky2rss.bot.model;

import com.hogemann.bsky2rss.rss.FeedItem;
import com.hogemann.bsky2rss.rss.YouTubeFeedExtractor;
import com.rometools.rome.feed.synd.SyndEntry;

import java.util.function.Function;

public enum FeedExtractor {
    YOUTUBE(YouTubeFeedExtractor::extract);

    private final Function<SyndEntry, FeedItem> extractor;

    FeedExtractor(Function<SyndEntry, FeedItem> extractor) {
        this.extractor = extractor;
    }

    public Function<SyndEntry, FeedItem> getExtractor() {
        return extractor;
    }
}
