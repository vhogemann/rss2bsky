package com.hogemann.bsky2rss.bsky.model.embed;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hogemann.bsky2rss.bsky.model.blob.Blob;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExternalDetails(String uri,
                              String title,
                              String description,
                              Blob thumb) {}
