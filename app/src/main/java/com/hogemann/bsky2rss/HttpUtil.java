package com.hogemann.bsky2rss;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.io.IOException;
import java.util.function.Function;

public class HttpUtil {
    public static <T,E> Result<T,E> execute(
            OkHttpClient client,
            Request request,
            ObjectMapper mapper,
            Function<Exception, E> exceptionMapper,
            Class<T> clazz,
            Class<E> errorClass) {
        try(var response = client.newCall(request).execute()) {
            if(response.isSuccessful() && response.body() != null) {
                var body = response.body().string();
                var result = mapper.readValue(body, clazz);
                return Result.ok(result);
            } else if (response.body() != null) {
                var body = response.body().string();
                var error = mapper.readValue(body, errorClass);
                return Result.error(error);
            }
        } catch (IOException e) {
            E error = exceptionMapper.apply(e);
            return Result.error(error);
        }
        return Result.empty();
    }
}
