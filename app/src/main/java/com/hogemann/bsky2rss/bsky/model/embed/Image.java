package com.hogemann.bsky2rss.bsky.model.embed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hogemann.bsky2rss.bsky.model.blob.Blob;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Image(
        Blob image,
        String alt,
        AspectRatio aspectRatio
) {
    public static int MAX_SIZE_BYTES = 1000000;
}
