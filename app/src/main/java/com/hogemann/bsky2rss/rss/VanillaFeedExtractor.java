package com.hogemann.bsky2rss.rss;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndLink;

import java.util.Optional;

public class VanillaFeedExtractor {
    public static FeedItem extract(SyndEntry entry) {
        final String title = getTitle(entry);
        final String link = getLink(entry);
        final String description = getDescription(entry);
        return new FeedItem(title, description, link, Optional.empty());
    }

    protected static String getLink(SyndEntry entry) {
        String link = entry.getLink();
        if(link == null && entry.getLinks() != null && !entry.getLinks().isEmpty()) {
            link = entry
                    .getLinks()
                    .stream()
                    .findFirst()
                    .map(SyndLink::getHref)
                    .orElse(null);
        }
        return link;
    }

    protected static String getTitle(SyndEntry entry) {
        String title = entry.getTitle();
        if(title == null && entry.getTitleEx() != null) {
            title = entry.getTitleEx().getValue();
        }
        return title;
    }

    protected static String getDescription(SyndEntry entry) {
        final String description = entry.getDescription().getValue();
        return description != null ? description.trim() : null;
    }
}
