package com.hogemann.bsky2rss.bsky.model.facet;

import java.util.ArrayList;
import java.util.List;

public record Facet(FacetFeature.ByteSlice index, List<FacetFeature> features) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private FacetFeature.ByteSlice index;
        private List<FacetFeature> features;

        public Builder index(FacetFeature.ByteSlice index) {
            this.index = index;
            return this;
        }

        public Builder features(List<FacetFeature> features) {
            this.features = features;
            return this;
        }

        public Builder addFeature(FacetFeature feature) {
            if(this.features == null) {
                this.features = new ArrayList<>();
            }
            this.features.add(feature);
            return this;
        }

        public Facet build() {
            return new Facet(index, features);
        }
    }
}
