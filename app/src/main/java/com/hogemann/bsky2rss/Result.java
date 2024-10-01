package com.hogemann.bsky2rss;

import java.util.function.Consumer;
import java.util.function.Function;

public class Result <T>{

    private final T value;
    private final Exception error;

    private Result(T value, Exception error) {
        this.value = value;
        this.error = error;
    }

    public static <T, E> Result<T> ok(T value) {
        return new Result<>(value, null);
    }

    public static <T, E> Result<T> error(Exception error) {
        return new Result<>(null, error);
    }

    public static <T,E> Result<T> empty() {
        return new Result<>(null,null);
    }

    public T get() { return this.value; }

    public Exception error() { return this.error; }

    public boolean isOk() { return this.error == null && this.value != null; }

    public boolean isError() { return this.error != null; }

    public boolean isEmpty() { return this.value == null && this.error == null; }

    public <U> Result<U> map(Function<T,U> mapper) {
        if(isOk()) {
            return ok(mapper.apply(value));
        } else {
            return error(error);
        }
    }

    public <U> Result<U> flatMap(Function<T,Result<U>> mapper) {
        if(isOk()) {
            return mapper.apply(value);
        } else {
            return error(error);
        }
    }

    public void ifOk(Consumer<T> consumer) {
        if(isOk()) {
            consumer.accept(value);
        }
    }

    public void ifOkOrElse(Consumer<T> consumer, Consumer<Exception> errorConsumer) {
        if(isOk()) {
            consumer.accept(value);
        } else {
            errorConsumer.accept(error);
        }
    }

    public T orElse(T other) {
        return isOk() ? value : other;
    }

}
