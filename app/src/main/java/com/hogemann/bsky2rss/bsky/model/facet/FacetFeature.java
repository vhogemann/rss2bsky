package com.hogemann.bsky2rss.bsky.model.facet;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "$type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FacetFeature.Mention.class, name = "app.bsky.richtext.facet#mention"),
        @JsonSubTypes.Type(value = FacetFeature.ByteSlice.class, name = "app.bsky.richtext.facet#byteSlice"),
        @JsonSubTypes.Type(value = FacetFeature.Tag.class, name = "app.bsky.richtext.facet#tag"),
        @JsonSubTypes.Type(value = FacetFeature.Link.class, name = "app.bsky.richtext.facet#link")
})
public interface FacetFeature {

    record Mention(String did) implements FacetFeature {
    }

    record ByteSlice(int byteStart, int byteEnd) implements FacetFeature{
        public ByteSlice {
            if (byteStart < 0) {
                throw new IllegalArgumentException("byteStart is negative");
            }
            if (byteEnd < 0) {
                throw new IllegalArgumentException("byteEnd is negative");
            }
        }
    }

    record Tag(String tag) implements FacetFeature {
        public Tag {
            if (tag != null && tag.length() > 640) {
                throw new IllegalArgumentException("tag is too long");
            }
        }
    }

    record Link(String uri) implements FacetFeature { }
}
