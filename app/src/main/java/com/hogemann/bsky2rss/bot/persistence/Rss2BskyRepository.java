package com.hogemann.bsky2rss.bot.persistence;

import com.hogemann.bsky2rss.bot.model.PublishedItem;
import com.hogemann.bsky2rss.bot.model.Source;

import java.util.List;
import java.util.UUID;

public interface Rss2BskyRepository {
    List<Source> listSources();
    List<PublishedItem> lastPublishedItem(UUID sourceId);
    void savePublishedItem(UUID sourceId, PublishedItem item);
}
