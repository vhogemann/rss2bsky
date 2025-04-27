package com.hogemann.bsky2rss.bsky;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.hogemann.bsky2rss.Result;
import com.hogemann.bsky2rss.bsky.model.AuthResponse;
import com.hogemann.bsky2rss.bsky.model.blob.UploadBlobResponse;
import com.hogemann.bsky2rss.bsky.model.record.CreateRecordRequest;
import com.hogemann.bsky2rss.bsky.model.record.CreateRecordResponse;
import com.hogemann.bsky2rss.bsky.model.record.Post;
import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.Buffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BlueSkyServiceTest {

    private static final String LOGIN_RESPONSE = """
            {
                "accessJwt": "123",
                "refreshJwt": "456",
                "did": "789"
            }
            """;

    private static final String CREATE_RECORD_RESPONSE = """
            {
                "uri": "uri",
                "cid": "cid",
                "commit": {
                    "cid": "cid",
                    "rev": "rev"
                },
                "validationStatus": "valid"
            }
            """;

    private static final String BLOB_UPLOAD_RESPONSE = """
            {
                "blob" : {
                    "$type" : "blob",
                        "ref" : {
                          "$link" : "abcdefg"
                        },
                        "mimeType" : "image/png",
                        "size" : 74540
                }
            }
            """;

    private MockWebServer server;
    private BlueSkyService service;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        final ObjectMapper mapper = new JsonMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(Instant.class, InstantDeserializer.INSTANT);
        mapper.registerModule(javaTimeModule);

        service = new BlueSkyService(
                server.url("/").toString(),
                new OkHttpClient(),
                mapper);
    }

    @Test
    void login() {
        server.enqueue(
                new MockResponse()
                        .setBody(LOGIN_RESPONSE)
                        .addHeader("Content-Type", "application/json")
        );
        Result<AuthResponse> login = service.login("user", "pass");
        assertTrue(login.isOk());
    }

    @Test
    void createRecord() {
        server.enqueue(
                new MockResponse()
                        .setBody(CREATE_RECORD_RESPONSE)
                        .addHeader("Content-Type", "application/json")
        );
        Result<CreateRecordResponse> record = service.createRecord(
                "token",
                CreateRecordRequest.ofPost(
                        "did",
                        Post.ofText("Hello, World!")
                ));
        assertTrue(record.isOk());
    }

    @Test
    void uploadBlob() {
        server.enqueue(
                new MockResponse()
                        .setBody(BLOB_UPLOAD_RESPONSE)
                        .addHeader("Content-Type", "application/json")
        );
        Result<UploadBlobResponse> blob = service.uploadBlob("token", getThumbImage());
        assertTrue(blob.isOk());
    }

    @Test
    void createPostWithLinkCard()
    {
        // Gets the HTML card from the URL
        server.enqueue(
                new MockResponse()
                        .setBody(getCardHtml())
                        .addHeader("Content-Type", "text/html")
        );

        // Download thumb image
        server.enqueue(
                new MockResponse()
                        // Create a buffer to store the image
                        .setBody(new Buffer().write(getThumbImage()))
                        .addHeader("Content-Type", "image/png")
        );

        // uploads the thumb image
        server.enqueue(
                new MockResponse()
                        .setBody(BLOB_UPLOAD_RESPONSE)
                        .addHeader("Content-Type", "application/json")
        );

        // Creates the post
        server.enqueue(
                new MockResponse()
                        .setBody(CREATE_RECORD_RESPONSE)
                        .addHeader("Content-Type", "application/json")
        );

        Result<CreateRecordResponse> record =
                service.createPostWithLinkCard(
                        "token",
                        "did",
                        "Hello, World!",
                        server.url("/").toString(),
                        null);

        assertTrue(record.isOk());
    }

    private String getCardHtml() {
        return """
                <html>
                    <head>
                        <meta property="og:title" content="Example Title">
                        <meta property="og:description" content="Example Description">
                        <meta property="og:image" content="%s">
                    </head>
                </html>
                """.formatted(server.url("/thumb.png").toString());
    }

    private byte[] getThumbImage() {
        // reads from the root classpath on resouces/duke.png
        try(InputStream is = BlueSkyServiceTest.class.getClassLoader().getResourceAsStream("duke.png")){
            assert is != null;
            return is.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}