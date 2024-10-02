#!/usr/bin/env sh
docker build -t rss2bsky .
docker run -it --rm -v $(pwd)/json:/root/dev/json rss2bsky