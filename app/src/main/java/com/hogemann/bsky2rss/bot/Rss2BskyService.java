package com.hogemann.bsky2rss.bot;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hogemann.bsky2rss.bot.model.PublishedItem;
import com.hogemann.bsky2rss.bot.model.Source;
import com.hogemann.bsky2rss.bot.persistence.Rss2BskyRepository;
import com.hogemann.bsky2rss.bsky.BlueSkyService;
import com.hogemann.bsky2rss.rss.FeedItem;
import com.hogemann.bsky2rss.rss.RssService;

import java.util.List;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;

@Singleton
public class Rss2BskyService {

    private static final Logger LOG = Logger.getLogger(Rss2BskyService.class.getName());

    private final RssService rssService;
    private final BlueSkyService blueSkyService;
    private final Rss2BskyRepository repository;

    @Inject
    public Rss2BskyService(RssService rssService, BlueSkyService blueSkyService, Rss2BskyRepository repository) {
        this.rssService = rssService;
        this.blueSkyService = blueSkyService;
        this.repository = repository;
    }

    public void run() {
        repository
                .listSources()
                .forEach(this::publish);
    }

    private void publish(Source source) {
        final List<String> lastPublished =
                repository
                        .lastPublishedItem(source.feedId()).stream()
                        .map(PublishedItem::url)
                        .toList();
        rssService
                .fetch(source.rssUrl(), source.feedExtractor().getExtractor())
                .ifOkOrElse(
                        feed -> {
                            List<FeedItem> items = feed.stream()
                                    .filter(item -> !lastPublished.contains(item.link()))
                                    .toList();
                            publish(source, items);
                        },
                        error -> LOG.log(SEVERE, "Failed to fetch feed", error)
                );
    }

    private void publish(Source source, List<FeedItem> items) {
        if (items.isEmpty()) {
            LOG.log(INFO, "No new items to publish for source {0}", source.name());
            return;
        }
        blueSkyService.login(source.bskyIdentity(), source.bskyPassword())
                .ifOkOrElse(
                        auth ->
                            items.forEach(item ->
                                blueSkyService.createPostWithLinkCard(
                                        source.bskyIdentity(),
                                        source.bskyPassword(),
                                        item.title(),
                                        item.link())
                                    .ifOkOrElse(
                                            response -> savePublishedItem(source, item),
                                            error -> LOG.log(SEVERE, "Failed to create post", error)
                                    )
                            )
                        ,
                        error -> LOG.log(SEVERE,"Failed to login to BlueSky", error)
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
