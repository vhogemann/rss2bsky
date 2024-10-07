package com.hogemann.bsky2rss.rss;

import com.rometools.rome.feed.synd.SyndEntry;
import org.jdom2.Element;

import static com.hogemann.bsky2rss.rss.VanillaFeedExtractor.getLink;
import static com.hogemann.bsky2rss.rss.VanillaFeedExtractor.getTitle;

public class YouTubeFeedExtractor {

    public static FeedItem extract(SyndEntry entry) {
        final String title = getTitle(entry);
        final String link = getLink(entry);
        final String description = getDescription(entry);
        return new FeedItem(title, description, link);
    }

    private static String getDescription(SyndEntry entry) {
        if(!entry.getForeignMarkup().isEmpty()) {
            return
                entry.getForeignMarkup().stream()
                    .filter(el -> el.getNamespace().getPrefix().equals("media"))
                    .findFirst()
                    .stream()
                    .flatMap(el -> el.getChildren().stream())
                    .filter(el -> el.getName().equals("description"))
                    .findFirst()
                    .map(Element::getText)
                    .orElse(null);

        }
        return null;
    }
}
