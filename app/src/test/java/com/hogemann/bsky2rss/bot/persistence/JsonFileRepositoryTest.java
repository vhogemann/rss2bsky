package com.hogemann.bsky2rss.bot.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.hogemann.bsky2rss.bot.model.FeedExtractor;
import com.hogemann.bsky2rss.bot.model.PublishedItem;
import com.hogemann.bsky2rss.bot.model.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonFileRepositoryTest {

    private static final String SOURCES = """
        [{
             "feedId": "test_feed",
             "name": "Example Feed",
             "rssUrl": "https://example/rss",
             "feedExtractor": "YOUTUBE",
             "bskyIdentity": "example.identity.com",
             "bskyPassword": "example-app-password"
        },
        {
             "feedId": "test_feed2",
             "name": "Example Feed2",
             "rssUrl": "https://example/rss",
             "feedExtractor": "VANILLA",
             "bskyIdentity": "example.identity.com",
             "bskyPassword": "example-app-password"
        }]
        """;

    private JsonFileRepository repository;

    @BeforeEach
    void setUp() throws IOException {
        // Json path points to system tmp folder
        final String jsonPath = System.getProperty("java.io.tmpdir");
        final ObjectMapper mapper = new JsonMapper();
        repository = new JsonFileRepository(mapper, jsonPath);
        Files.writeString(Paths.get(jsonPath + "/source.json"), SOURCES);

        // deletes every *.ndjson file in the tmp folder
        Files.list(Paths.get(jsonPath))
                .filter(path -> path.toString().endsWith(".ndjson"))
                .forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Test
    void listSources() {
        final List<Source> sources = repository.listSources();
        assertEquals(2, sources.size());
        final Source source = sources.getFirst();
        assertEquals("test_feed", source.feedId());
        assertEquals("Example Feed", source.name());
        assertEquals("https://example/rss", source.rssUrl());
        assertEquals(FeedExtractor.YOUTUBE, source.feedExtractor());
        assertEquals("example.identity.com", source.bskyIdentity());
        assertEquals("example-app-password", source.bskyPassword());
    }

    @Test
    void getLastLines() {
        final PublishedItem item = new PublishedItem("test_feed","https://example.com/item", "Example Item");
        repository.savePublishedItem("test_feed", item);
        final List<PublishedItem> items = repository.lastPublishedItem("test_feed");
        assertEquals(item, items.getFirst());
    }

    @Test
    void lastPublishedItem() {
        final PublishedItem item = new PublishedItem("test_feed","https://example.com/item", "Example Item");
        repository.savePublishedItem("test_feed", item);
        final List<PublishedItem> items = repository.lastPublishedItem("test_feed");
        assertEquals(item, items.getFirst());
    }

    @Test
    void savePublishedItem() {
        final PublishedItem item = new PublishedItem("test_feed","https://example.com/item", "Example Item");
        repository.savePublishedItem("test_feed", item);
        final List<PublishedItem> items = repository.lastPublishedItem("test_feed");
        assertEquals(item, items.getFirst());
    }
}