package com.hogemann.bsky2rss.bsky.model.blob;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/*
{
  "blob" : {
    "$type" : "blob",
    "ref" : {
      "$link" : "bafkreibnuv5kefrt6jlzc5oty36qfzvc23lvdoaj3vkdqqzneigt5ftaru"
    },
    "mimeType" : "image/png",
    "size" : 74540
  }
}
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "$type")
@JsonTypeName("blob")
public record Blob(RefLink ref, String mimeType, int size) { }
