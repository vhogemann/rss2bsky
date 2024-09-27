package com.hogemann.bsky2rss.bsky.model.embed;

import com.hogemann.bsky2rss.bsky.model.blob.Blob;

public record External(ExternalDetails external) implements Embed{
        public static External of(String uri, String title, String description, Blob thumb) {
                return new External(new ExternalDetails(uri, title, description, thumb));
        }
}
