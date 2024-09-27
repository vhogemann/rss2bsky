package com.hogemann.bsky2rss.bsky.model.embed;

import com.hogemann.bsky2rss.bsky.model.blob.Blob;

import java.util.List;

public record Images(List<Image> images) implements Embed {
    public static Images ofImage(String alt, Blob blob) {
        return new Images(List.of(new Image(blob, alt, null)));
    }
}
