package com.hogemann.bsky2rss.rss;

import com.hogemann.bsky2rss.Result;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RssServiceTest {

    private MockWebServer server;
    private RssService rssService;

    @BeforeEach
    void setUp() {
        server = new MockWebServer();
        rssService = new RssService(new OkHttpClient());
    }

    @Test
    public void testFetchVanillaFeed() {
        server.enqueue(new MockResponse().setBody(getMockFeed("vanilla.xml")));

        final Result<List<FeedItem>> feed = rssService.fetch(server.url("/").toString(), VanillaFeedExtractor::extract);
        assertTrue(feed.isOk());

        final List<FeedItem> items = feed.get();
        assertFalse(items.isEmpty());
        assertEquals(3, items.size());

        assertEquals(
                items.getFirst(),
                new FeedItem(
                        "Rinha de Backend 2024 - F#",
                        "A Rinha de Backend é um evento organizado pelo Francisco Zanfrancheschi. As regras são simples, você precisa criar uma API rodando em docker compose, seguindo a arquitetura mínima pedida, e que sobreviva a um teste de carga previamente escrito.",
                        server.url("/hacking/fsharp/2024/02/25/rinha-de-backend-2024.html").toString()
                        ));
    }

    @Test
    public void testFetchYoutubeFeed() {
        server.enqueue(new MockResponse().setBody(getMockFeed("youtube.xml")));

        final Result<List<FeedItem>> feed = rssService.fetch(server.url("/").toString(), YouTubeFeedExtractor::extract);
        assertTrue(feed.isOk());

        final List<FeedItem> items = feed.get();
        assertFalse(items.isEmpty());
        assertEquals(8, items.size());

        assertEquals(
                items.getFirst(),
                new FeedItem(
                        "How do QR codes work? (I built one myself to find out)",
                        "Video Description",
                        "https://www.youtube.com/watch?v=w5ebcowAJD8"
                        ));
    }

    private String getMockFeed(String feedName) {
         try (InputStream stream = RssServiceTest.class.getClassLoader().getResourceAsStream(feedName)){
            return new String(stream != null ? stream.readAllBytes() : null);
         } catch (IOException e) {
             throw new RuntimeException(e);
         }
    }
    
}