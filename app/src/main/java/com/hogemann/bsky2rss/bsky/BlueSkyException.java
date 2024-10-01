package com.hogemann.bsky2rss.bsky;

import com.hogemann.bsky2rss.bsky.model.RequestError;

public class BlueSkyException extends RuntimeException {

    private final RequestError requestError;

    public BlueSkyException(RequestError error) {
        super(error.message());
        this.requestError = error;
    }

    public RequestError getRequestError() {
        return requestError;
    }
}
