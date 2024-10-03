package com.hogemann.bsky2rss.bsky.model.record;

import com.hogemann.bsky2rss.bsky.model.Commit;
import com.hogemann.bsky2rss.bsky.model.ValidationStatus;

public record CreateRecordResponse(
        String uri,
        String cid,
        Commit commit,
        ValidationStatus validationStatus
) {
}
