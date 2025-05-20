package com.hogemann.bsky2rss;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URI;

public class UrlUtil {
    public static String toAbsoluteUrl(@NonNull String baseUrl, @NonNull String itemUrl) {
        if (itemUrl.contains("://")) {
            return itemUrl;
        }
        final URI feedUri = URI.create(baseUrl);
        return
                feedUri.getScheme() +
                        "://" +
                        feedUri.getHost() +
                        (feedUri.getPort() > 0 ? ":" + feedUri.getPort() : "") +
                        (itemUrl.startsWith("/")
                                ? itemUrl
                                : "/" + itemUrl);
    }
}
