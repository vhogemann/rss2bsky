package com.hogemann.bsky2rss;

import java.util.function.Consumer;
import java.util.function.Function;

public class Result <V>{

    private final V value;
    private final Exception error;

    private Result(V value, Exception error) {
        this.value = value;
        this.error = error;
    }

    public static <V> Result<V> ok(V value) {
        return new Result<>(value, null);
    }

    public static <V> Result<V> error(Exception error) {
        return new Result<>(null, error);
    }

    public static <V> Result<V> empty() {
        return new Result<>(null,null);
    }

    public V get() { return this.value; }

    public Exception error() { return this.error; }

    public boolean isOk() { return this.error == null && this.value != null; }

    public boolean isEmpty() { return this.value == null && this.error == null; }

    public <U> Result<U> map(Function<V,U> mapper) {
        if(isOk()) {
            return ok(mapper.apply(value));
        } else {
            return error(error);
        }
    }

    public <U> Result<U> flatMap(Function<V,Result<U>> mapper) {
        if(isOk()) {
            return mapper.apply(value);
        } else {
            return error(error);
        }
    }

    public void ifOkOrElse(Consumer<V> consumer, Consumer<Exception> errorConsumer) {
        if(isOk()) {
            consumer.accept(value);
        } else {
            errorConsumer.accept(error);
        }
    }

}
