package com.hogemann.bsky2rss.bsky.model.record;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "$type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Post.class, name = "app.bsky.feed.post"),
        @JsonSubTypes.Type(value = Repost.class, name = "app.bsky.feed.reposrepostt"),
        @JsonSubTypes.Type(value = Like.class, name = "app.bsky.feed.like")
})
public interface Record { }
