package com.hogemann.bsky2rss;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Result <T,E>{

    private final T value;
    private final E error;

    private Result(T value, E error) {
        this.value = value;
        this.error = error;
    }

    public static <T, E> Result<T, E> ok(T value) {
        return new Result<>(value, null);
    }

    public static <T, E> Result<T, E> error(E error) {
        return new Result<>(null, error);
    }

    public static <T,E> Result<T,E> empty() {
        return new Result<>(null,null);
    }

    public T get() { return this.value; }

    public E error() { return this.error; }

    public boolean isOk() { return this.error == null && this.value != null; }

    public boolean isError() { return this.error != null; }

    public boolean isEmpty() { return this.value == null && this.error == null; }

    public <U> Result<U,E> map(Function<T,U> mapper) {
        if(isOk()) {
            return ok(mapper.apply(value));
        } else {
            return error(error);
        }
    }

    public <U,X> Result<U,X> flatMap(Function<T,Result<U,X>> mapper ) {
        return mapper.apply(value);
    }

    public void ifOk(Consumer<T> consumer) {
        if(isOk()) {
            consumer.accept(value);
        }
    }

    public void ifOkOrElse(Consumer<T> consumer, Consumer<E> errorConsumer) {
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
