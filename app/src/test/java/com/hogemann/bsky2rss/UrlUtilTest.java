package com.hogemann.bsky2rss;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrlUtilTest {

    @Test
    public void toAbsoluteUrl() {
        String  absoluteUrl = UrlUtil.toAbsoluteUrl("https://test.com/rss/atom.xml", "/some/relative/path");
        assertEquals("https://test.com/some/relative/path", absoluteUrl);
    }

}