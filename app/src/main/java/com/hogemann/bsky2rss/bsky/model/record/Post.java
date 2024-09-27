package com.hogemann.bsky2rss.bsky.model.record;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hogemann.bsky2rss.bsky.model.Entity;
import com.hogemann.bsky2rss.bsky.model.Labels;
import com.hogemann.bsky2rss.bsky.model.ReplyRef;
import com.hogemann.bsky2rss.bsky.model.blob.Blob;
import com.hogemann.bsky2rss.bsky.model.embed.Embed;
import com.hogemann.bsky2rss.bsky.model.embed.External;
import com.hogemann.bsky2rss.bsky.model.embed.Images;
import com.hogemann.bsky2rss.bsky.model.facet.Facet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// https://github.com/bluesky-social/atproto/blob/main/lexicons/app/bsky/feed/post.json
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Post  (
        String text,
        List<Entity> entities,
        List<Facet> facets,
        ReplyRef reply,
        Embed embed,
        List<String> langs,
        Labels labels,
        List<String> tags,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
        Instant createdAt
        ) implements Record {

        public static Post ofText(String text) {
                return Post.builder()
                        .text(text)
                        .createdAt(Instant.now())
                        .build();
        }

        public static Post ofTextWithImage(String lang, String text, String alt, Blob image) {
                return Post.builder()
                        .text(text)
                        .addLang(lang)
                        .embed(Images.ofImage(alt, image))
                        .createdAt(Instant.now())
                        .build();
        }

        public static Post ofTextWithExternal(String lang, String text, External external) {
                return Post.builder()
                        .text(text)
                        .addLang(lang)
                        .embed(external)
                        .createdAt(Instant.now())
                        .build();
        }

        public static Builder builder() {
                return new Builder();
        }

        public static class Builder {
                private String text;
                private List<Entity> entities;
                private List<Facet> facets;
                private ReplyRef reply;
                private Embed embed;
                private List<String> langs;
                private Labels labels;
                private List<String> tags;
                private Instant createdAt;

                public Builder text(String text) {
                        this.text = text;
                        return this;
                }

                public Builder entities(List<Entity> entities) {
                        this.entities = entities;
                        return this;
                }

                public Builder facets(List<Facet> facets) {
                        this.facets = facets;
                        return this;
                }

                public Builder reply(ReplyRef reply) {
                        this.reply = reply;
                        return this;
                }

                public Builder embed(Embed embed) {
                        this.embed = embed;
                        return this;
                }

                public Builder langs(List<String> langs) {
                        this.langs = langs;
                        return this;
                }

                public Builder labels(Labels labels) {
                        this.labels = labels;
                        return this;
                }

                public Builder addLang(String lang) {
                        if(this.langs == null) {
                                this.langs = new ArrayList<>();
                        }
                        this.langs.add(lang);
                        return this;
                }

                public Builder tags(List<String> tags) {
                        this.tags = tags;
                        return this;
                }

                public Builder addTag(String tag) {
                        if(this.tags == null) {
                                this.tags = new ArrayList<>();
                        }
                        this.tags.add(tag);
                        return this;
                }

                public Builder createdAt(Instant createdAt) {
                        this.createdAt = createdAt;
                        return this;
                }

                public Post build() {
                        return new Post(text, entities, facets, reply, embed, langs, labels, tags, createdAt);
                }
        }
}
