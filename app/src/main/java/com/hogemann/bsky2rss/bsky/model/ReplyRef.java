package com.hogemann.bsky2rss.bsky.model;

public record ReplyRef(
        StrongRef root,
        StrongRef parent
) { }
