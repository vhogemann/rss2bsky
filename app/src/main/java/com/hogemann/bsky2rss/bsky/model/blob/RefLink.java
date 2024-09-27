package com.hogemann.bsky2rss.bsky.model.blob;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RefLink(@JsonProperty("$link") String link) {
}
