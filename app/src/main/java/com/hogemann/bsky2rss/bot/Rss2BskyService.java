package com.hogemann.bsky2rss.bot;

import com.hogemann.bsky2rss.UrlUtil;
import com.hogemann.bsky2rss.bot.model.PublishedItem;
import com.hogemann.bsky2rss.bot.model.Source;
import com.hogemann.bsky2rss.bot.persistence.Rss2BskyRepository;
import com.hogemann.bsky2rss.bsky.BlueSkyService;
import com.hogemann.bsky2rss.rss.FeedItem;
import com.hogemann.bsky2rss.rss.RssService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Rss2BskyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Rss2BskyService.class);

    private final RssService rssService;
    private final BlueSkyService blueSkyService;
    private final Rss2BskyRepository repository;

    public Rss2BskyService(RssService rssService, BlueSkyService blueSkyService, Rss2BskyRepository repository) {
        this.rssService = rssService;
        this.blueSkyService = blueSkyService;
        this.repository = repository;
    }

    public void run() {
        repository
                .listSources()
                .ifOkOrElse(
                    sources -> sources.forEach(this::publish),
                    error -> LOGGER.error("Failed to list sources", error)
                );
    }

    private void publish(Source source) {
        LOGGER.info("Fetching feed for source {}", source.name());
        repository
                .lastPublishedItem(source.feedId())
                .map(items -> items.stream().map(PublishedItem::url).toList())
                .flatMap(lastPublished ->
                    rssService
                        .fetch(source.rssUrl(), source.feedExtractor().getExtractor())
                        .map(feed -> feed.stream()
                                .filter(item -> !lastPublished.contains(item.link()))
                                .toList()
                        )
                )
                .ifOkOrElse(
                    items -> publish(source, items),
                    error -> LOGGER.error("Failed to list last published items", error)
                );
    }

    private void publish(Source source, List<FeedItem> items) {
        if (items.isEmpty()) {
            LOGGER.info("No new items to publish for source {}", source.feedId());
            return;
        }
        LOGGER.info("Publishing {} new items for source {}", items.size(), source.feedId());
        blueSkyService.login(source.bskyIdentity(), source.bskyPassword())
                .ifOkOrElse(
                        auth ->
                            items.forEach(item ->
                                blueSkyService.createPostWithLinkCard(
                                        auth.accessJwt(),
                                        auth.did(),
                                        item.title(),
                                        UrlUtil.toAbsoluteUrl(source.rssUrl(), item.link()),
                                        item.thumbnail().orElse(null))
                                    .ifOkOrElse(
                                            response -> savePublishedItem(source, item),
                                            error -> LOGGER.error("Failed to publish item: ", error)
                                    )
                            )
                        ,
                        error -> LOGGER.error("Failed to login to BlueSky: ", error)
                );
    }
    private void savePublishedItem(Source source, FeedItem item) {
        repository.savePublishedItem(source.feedId(), new PublishedItem(
                source.feedId(),
                item.title(),
                item.link()
        ));
    }
}
