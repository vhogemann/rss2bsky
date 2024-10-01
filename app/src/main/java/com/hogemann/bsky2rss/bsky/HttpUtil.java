package com.hogemann.bsky2rss.bsky;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hogemann.bsky2rss.Result;
import com.hogemann.bsky2rss.bsky.model.RequestError;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.function.Function;

public class HttpUtil {
    public static <T> Result<T> execute(
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
}
