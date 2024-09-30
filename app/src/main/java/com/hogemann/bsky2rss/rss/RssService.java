package com.hogemann.bsky2rss.rss;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hogemann.bsky2rss.Result;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Singleton
public class RssService {

    private final OkHttpClient client;

    @Inject
    public RssService(OkHttpClient client) {
        this.client = client;
    }

    public Result<List<FeedItem>, Exception> fetch(String url, Function<SyndEntry, FeedItem> extractor) {
        return fetchFeed(url)
                .flatMap(rss -> {
                    try {
                        final SyndFeed feed = new SyndFeedInput().build(new XmlReader(new ByteArrayInputStream(rss.getBytes())));
                        final List<FeedItem> items = feed.getEntries().stream().map(extractor).toList();
                        return Result.ok(items);
                    } catch (FeedException | IOException e) {
                        return Result.error(e);
                    }
                });
    }

    private Result<String, Exception> fetchFeed(String url) {
        final Request request = new Request.Builder().url(url).get().build();
        try(var result = client.newCall(request).execute()) {
            if(result.isSuccessful() && result.body() != null) {
                return Result.ok(result.body().string());
            } else {
                return Result.error(new Exception("Failed to fetch feed"));
            }
        } catch (IOException e) {
            return Result.error(e);
        }
    }

}
