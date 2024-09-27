package com.hogemann.bsky2rss.bsky.model.record;

public record CreateRecordRequest(
        String repo,
        String collection,
        Record record
) {
    public static CreateRecordRequest ofPost(String did, Post post) {
        return new CreateRecordRequest(did, "app.bsky.feed.post", post);
    }

    public static CreateRecordRequest ofRepost(String did, String collection, Repost repost) {
        return new CreateRecordRequest(did, "app.bsky.feed.repost", repost);
    }

    public static CreateRecordRequest ofLike(String did, String collection, Like like) {
        return new CreateRecordRequest(did, "app.bsky.feed.like", like);
    }
}
