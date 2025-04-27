package com.hogemann.bsky2rss.rss;

import com.rometools.rome.feed.synd.SyndEntry;
import org.jdom2.Element;

import java.util.Optional;

import static com.hogemann.bsky2rss.rss.VanillaFeedExtractor.getLink;
import static com.hogemann.bsky2rss.rss.VanillaFeedExtractor.getTitle;

public class YouTubeFeedExtractor {

    public static FeedItem extract(SyndEntry entry) {
        final String title = getTitle(entry);
        final String link = getLink(entry);
        final String description = getDescription(entry);
        final Optional<String> thumbnail = getThumbnail(entry);
        return new FeedItem(title, description, link, thumbnail);
    }

    private static String getDescription(SyndEntry entry) {
        return getMediaElement(entry, "description")
                .map(Element::getText)
                .orElse(null);
    }

    private static Optional<String> getThumbnail(SyndEntry entry) {
        return getMediaElement(entry, "thumbnail")
                .map(el -> el.getAttributeValue("url"));
    }

    private static Optional<Element> getMediaElement(SyndEntry entry, String mediaElement) {
            return
                    entry.getForeignMarkup().stream()
                            .filter(el -> el.getNamespace().getPrefix().equals("media"))
                            .findFirst()
                            .stream()
                            .flatMap(el -> el.getChildren().stream())
                            .filter(el -> el.getName().equals(mediaElement))
                            .findFirst();

    }
}
