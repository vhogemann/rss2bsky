package com.hogemann.bsky2rss.bot.persistence;

import com.hogemann.bsky2rss.Result;
import com.hogemann.bsky2rss.bot.model.PublishedItem;
import com.hogemann.bsky2rss.bot.model.Source;

import java.util.List;
import java.util.UUID;

public interface Rss2BskyRepository {
    Result<List<Source>> listSources();
    Result<List<PublishedItem>> lastPublishedItem(String sourceId);
    void savePublishedItem(String sourceId, PublishedItem item);
}
