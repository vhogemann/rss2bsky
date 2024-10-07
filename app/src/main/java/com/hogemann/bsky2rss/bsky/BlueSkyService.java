package com.hogemann.bsky2rss.bsky;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hogemann.bsky2rss.Result;
import com.hogemann.bsky2rss.bsky.model.AuthRequest;
import com.hogemann.bsky2rss.bsky.model.AuthResponse;
import com.hogemann.bsky2rss.bsky.model.record.CreateRecordResponse;
import com.hogemann.bsky2rss.bsky.model.RequestError;
import com.hogemann.bsky2rss.bsky.model.blob.UploadBlobResponse;
import com.hogemann.bsky2rss.bsky.model.embed.External;
import com.hogemann.bsky2rss.bsky.model.record.CreateRecordRequest;
import com.hogemann.bsky2rss.bsky.model.record.Post;
import okhttp3.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;

public class BlueSkyService {

    private static final int IMAGE_MAX_SIZE_BYTES = 1024 * 1024;
    private static final int IMAGE_MAX_WIDTH = 1024;
    private static final int IMAGE_MAX_HEIGHT = 1024;

    private final String baseUrl;
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public BlueSkyService(String baseUrl, OkHttpClient client, ObjectMapper mapper) {
        this.baseUrl = baseUrl;
        this.client = client;
        this.mapper = mapper;
    }

    public Result<AuthResponse> login(String identity, String password) {
        AuthRequest authRequest = new AuthRequest(identity, password);
        try {
            return execute(
                    client,
                    new Request.Builder()
                            .url(baseUrl + "/com.atproto.server.createSession")
                            .post(RequestBody.create(mapper.writeValueAsString(authRequest), MediaType.parse("application/json")))
                            .build(),
                    mapper,
                    AuthResponse.class);
        } catch (JsonProcessingException e) {
            return Result.error(e);
        }
    }

    public Result<CreateRecordResponse> createRecord(String token, CreateRecordRequest request) {
        try {
            String json = mapper.writeValueAsString(request);
            return execute(
                    client,
                    new Request.Builder()
                            .url(baseUrl + "/com.atproto.repo.createRecord")
                            .addHeader("Authorization", "Bearer " + token)
                            .post(RequestBody.create(json, MediaType.parse("application/json")))
                            .build(),
                    mapper,
                    CreateRecordResponse.class
            );
        } catch (JsonProcessingException e) {
            return Result.error(e);
        }
    }

    public Result<UploadBlobResponse> uploadBlob(String token, byte[] blob) {
        return execute(
                client,
                new Request.Builder()
                        .url(baseUrl + "/com.atproto.repo.uploadBlob")
                        .addHeader("Authorization", "Bearer " + token)
                        .post(RequestBody.create(blob, MediaType.parse("application/octet-stream")))
                        .build(),
                mapper,
                UploadBlobResponse.class
        );
    }

    public Result<CreateRecordResponse> createPostWithLinkCard(String token, String did, String text, String uri) {
        return downloadCardInfo(uri)
                .flatMap(cardInfo ->
                        downloadImage(cardInfo.thumb)
                                .flatMap(image ->
                                        ImageService.resize(
                                                image,
                                                IMAGE_MAX_WIDTH,
                                                IMAGE_MAX_HEIGHT,
                                                IMAGE_MAX_SIZE_BYTES))
                                .flatMap(image ->
                                        uploadBlob(token, image)
                                                .flatMap(blob -> {
                                                    External external = External.of(uri, cardInfo.title, cardInfo.description, blob.blob());
                                                    return createRecord(token, CreateRecordRequest.ofPost(
                                                            did,
                                                            Post.ofTextWithExternal("en", text, external)
                                                    ));
                                                })

                                ));
    }

    private Result<byte[]> downloadImage(String url) {
        if (url == null) {
            return Result.error(new Exception("No image to download"));
        }
        try (Response response = client.newCall(
                new Request.Builder()
                        .url(url)
                        .get()
                        .build()
        ).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return Result.ok(response.body().bytes());
            } else {
                return Result.error(new Exception("Failed to fetch image"));
            }
        } catch (IOException e) {
            return Result.error(e);
        }
    }

    private Result<CardInfo> downloadCardInfo(String url) {
        try (Response response = client.newCall(
                new Request.Builder()
                        .url(url)
                        .get()
                        .build()
        ).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                Document doc = Jsoup.parse(response.body().string());
                String title = null;
                var titleElement = doc.selectFirst("meta[property=og:title]");
                if (titleElement != null) {
                    title = titleElement.attr("content");
                }
                var descriptionElement = doc.selectFirst("meta[property=og:description]");
                String description = null;
                if (descriptionElement != null) {
                    description = descriptionElement.attr("content");
                }
                var thumbElement = doc.selectFirst("meta[property=og:image]");
                String thumb = null;
                if (thumbElement != null) {
                    thumb = toAbsoluteUrl(url, thumbElement.attr("content"));
                } else {
                    var twitterImageElement = doc.selectFirst("meta[name=twitter:image]");
                    if (twitterImageElement != null) {
                        thumb = toAbsoluteUrl(url, twitterImageElement.attr("content"));
                    }
                }
                return Result.ok(new CardInfo(title, description, thumb));
            } else {
                return Result.error(new Exception("Failed to fetch link"));
            }
        } catch (IOException e) {
            return Result.error(e);
        }
    }

    private String toAbsoluteUrl(@NonNull String baseUrl, @NonNull String itemUrl) {
        if(itemUrl.contains("://")) {
            return itemUrl;
        }
        final URI feedUri = URI.create(baseUrl);
        return
                feedUri.getScheme() +
                        "://" +
                        feedUri.getHost() +
                        (feedUri.getPort() > 0 ? ":" + feedUri.getPort() : "") +
                        (itemUrl.startsWith("/")
                                ? itemUrl
                                : "/" + itemUrl);
    }

    private static <T> Result<T> execute(
            OkHttpClient client,
            Request request,
            ObjectMapper mapper,
            Class<T> clazz) {
        try(var response = client.newCall(request).execute()) {
            if(response.isSuccessful() && response.body() != null) {
                var body = response.body().string();
                var result = mapper.readValue(body, clazz);
                return Result.ok(result);
            } else if (response.body() != null) {
                var body = response.body().string();
                var error = mapper.readValue(body, RequestError.class);
                return Result.error(new BlueSkyException(error));
            }
        } catch (IOException e) {
            return Result.error(e);
        }
        return Result.empty();
    }

    private record CardInfo(String title, String description, String thumb) { }

}
