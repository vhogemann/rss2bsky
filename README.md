![img.png](img.png)

# rss2bsky

rss2bsky is a Java application that fetches RSS feed items and posts them to BlueSky.

## Features

- Fetches RSS feed items from YouTube.
- Posts feed items to BlueSky with link cards.

## Requirements

- Java 21 or later
- Gradle 8.10.1 or later

## Running

This application is set up to run within a **Docker** container. It's the easiest
and recommended way to run it.

### Configuration

Before running the application, you need to set up a `source.json` file in the `/json` directory.
This file should contain the following structure:

```json
[
  {
    "feedId": "example_feed",
    "name": "Example Feed",
    "rssUrl": "https://www.youtube.com/feeds/videos.xml?playlist_id=PLAYLIST_ID",
    "feedExtractor": "YOUTUBE",
    "bskyIdentity": "example.identity.com",
    "bskyPassword": "example-app-password"
  }
]
```

It is a JSON array of objects, where each object represents a feed to be fetched and posted to BlueSky. 
Briefly, the configuration fields are:

| Field          | Description                                                                       |
|----------------|-----------------------------------------------------------------------------------|
| `feedId`       | A unique identifier for the feed.                                                 |
| `name`         | The name of the feed.                                                             |
| `rssUrl`       | The URL of the RSS feed.                                                          |
| `feedExtractor`| The feed extractor to use. Currently, only `YOUTUBE` and `VANILLA` are supported. |
| `bskyIdentity` | The BlueSky username to use.                                                      |
| `bskyPassword` | The BlueSky App password to use.                                                  |

#### Extractors

Extractor define how the feed items are handled, and the fields that will be used to fetch the BlueSky post
content and link embeds. Currently, the following extractors are available:

- `YOUTUBE`: Extracts feed items from a YouTube Channel/Playlist RSS feed.
- `VANILLA`: Extracts feed items from a generic RSS feed.

#### Running

To build and run the container, execute the script:

```bash
./run.sh
```

This script will build the Docker image and run the container. The application will start fetching feed items
and posting them to BlueSky.

To keep track of what was posted, the application will create a `.ndjson` file named after the feed ID in the `/json`
directory. This file will contain the feed items that were posted to BlueSky.

The idea is to run this application periodically, so it can fetch new feed items and post them to BlueSky.