package com.hogemann.bsky2rss.bsky;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hogemann.bsky2rss.Result;
import com.hogemann.bsky2rss.bsky.model.*;
import com.hogemann.bsky2rss.bsky.model.blob.UploadBlobResponse;
import com.hogemann.bsky2rss.bsky.model.embed.External;
import com.hogemann.bsky2rss.bsky.model.record.CreateRecordRequest;
import com.hogemann.bsky2rss.bsky.model.record.Post;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class BlueSkyService {
    private final String baseUrl;
    private final OkHttpClient client;
    private final ObjectMapper mapper;

    public BlueSkyService(String baseUrl, OkHttpClient client, ObjectMapper mapper) {
        this.baseUrl = baseUrl;
        this.client = client;
        this.mapper = mapper;
    }

    private static RequestError exceptionMapper(Exception ex) {
        return new RequestError(ex.getMessage(), ex.getClass().getName());
    }

    public Result<AuthResponse, RequestError> login(String identity, String password) {
        AuthRequest authRequest = new AuthRequest(identity, password);
        try {
            return HttpUtil.execute(
                    client,
                    new Request.Builder()
                            .url(baseUrl + "/com.atproto.server.createSession")
                            .post(RequestBody.create(mapper.writeValueAsString(authRequest), MediaType.parse("application/json")))
                            .build(),
                    mapper,
                    BlueSkyService::exceptionMapper,
                    AuthResponse.class,
                    RequestError.class
            );
        } catch (JsonProcessingException e) {
            return Result.error(exceptionMapper(e));
        }
    }

    public Result<CreateRecordResponse, RequestError> createRecord(String token, CreateRecordRequest request) {
        try {
            String json = mapper.writeValueAsString(request);
            return HttpUtil.execute(
                    client,
                    new Request.Builder()
                            .url(baseUrl + "/com.atproto.repo.createRecord")
                            .addHeader("Authorization", "Bearer " + token)
                            .post(RequestBody.create(json, MediaType.parse("application/json")))
                            .build(),
                    mapper,
                    BlueSkyService::exceptionMapper,
                    CreateRecordResponse.class,
                    RequestError.class
            );
        } catch (JsonProcessingException e) {
            return Result.error(exceptionMapper(e));
        }
    }

    public Result<UploadBlobResponse, RequestError> uploadBlob(String token, byte[] blob) {
        return HttpUtil.execute(
                client,
                new Request.Builder()
                        .url(baseUrl + "/com.atproto.repo.uploadBlob")
                        .addHeader("Authorization", "Bearer " + token)
                        .post(RequestBody.create(blob, MediaType.parse("application/octet-stream")))
                        .build(),
                mapper,
                BlueSkyService::exceptionMapper,
                UploadBlobResponse.class,
                RequestError.class
        );
    }

    public Result<CreateRecordResponse, RequestError> createPostWithLinkCard(String token, String did, String text, String uri) {
        return downloadCardInfo(uri)
                .flatMap(cardInfo ->
                    downloadImage(cardInfo.thumb)
                        .flatMap(image -> uploadBlob(token, image))
                        .flatMap(blob -> {
                            External external = External.of(uri, cardInfo.title, cardInfo.description, blob.blob());
                            return createRecord(token, CreateRecordRequest.ofPost(
                                    did,
                                    Post.ofTextWithExternal("en", text, external)
                            ));
                }));
    }

    private Result<byte[], Exception> downloadImage(String url) {
        if(url == null) {
            return Result.error(new Exception("No image to download"));
        }
        try (Response response = client.newCall(
                new Request.Builder()
                        .url(url)
                        .get()
                        .build()
        ).execute()) {
            if(response.isSuccessful() && response.body() != null) {
                return Result.ok(response.body().bytes());
            } else {
                return Result.error(new Exception("Failed to fetch image"));
            }
        } catch (IOException e) {
            return Result.error(e);
        }
    }

    private Result<CardInfo, Exception> downloadCardInfo(String url){
        try (Response response = client.newCall(
                new Request.Builder()
                        .url(url)
                        .get()
                        .build()
        ).execute()) {
            if(response.isSuccessful() && response.body() != null) {
                Document doc = Jsoup.parse(response.body().string());
                String title = null;
                var titleElement = doc.selectFirst("meta[property=og:title]");
                if( titleElement != null) {
                    title = titleElement.attr("content");
                }
                var descriptionElement = doc.selectFirst("meta[property=og:description]");
                String description = null;
                if( descriptionElement != null) {
                    description = descriptionElement.attr("content");
                }
                var thumbElement = doc.selectFirst("meta[property=og:image]");
                String thumb = null;
                if( thumbElement != null) {
                    thumb = thumbElement.attr("content");
                } else {
                    var twitterImageElement = doc.selectFirst("meta[name=twitter:image]");
                    if(twitterImageElement != null) {
                        thumb = twitterImageElement.attr("content");
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

    private record CardInfo(String title, String description, String thumb) {}

}
