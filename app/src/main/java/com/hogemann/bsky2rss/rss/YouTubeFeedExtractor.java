package com.hogemann.bsky2rss.rss;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndLink;
import org.jdom2.Element;

public class YouTubeFeedExtractor {

    public static FeedItem extract(SyndEntry entry) {
        final String title = getTitle(entry);
        final String link = getLink(entry);
        final String description = getDescription(entry);
        return new FeedItem(title, description, link);
    }
    private static String getLink(SyndEntry entry) {
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

    private static String getTitle(SyndEntry entry) {
        String title = entry.getTitle();
        if(title == null && entry.getTitleEx() != null) {
            title = entry.getTitleEx().getValue();
        }
        return title;
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
