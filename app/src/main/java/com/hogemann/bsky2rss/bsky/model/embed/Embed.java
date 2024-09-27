package com.hogemann.bsky2rss.bsky.model.embed;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Images.class, name = "app.bsky.embed.images"),
        @JsonSubTypes.Type(value = External.class, name = "app.bsky.embed.external")
})
public interface Embed { }
