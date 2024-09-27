package com.hogemann.bsky2rss.bsky.model;

public record CreateRecordResponse(
        String uri,
        String cid,
        Commit commit,
        ValidationStatus validationStatus
) {
}
