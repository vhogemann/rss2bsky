package com.hogemann.bsky2rss.bot.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hogemann.bsky2rss.Result;
import com.hogemann.bsky2rss.bot.model.PublishedItem;
import com.hogemann.bsky2rss.bot.model.Source;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class JsonFileRepository implements Rss2BskyRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonFileRepository.class);
    private final static String SOURCES_FILE = "source.json";
    private final ObjectMapper mapper;
    private final String path;

    public JsonFileRepository(
            ObjectMapper mapper,
            String path) {
        this.mapper = mapper;
        this.path = path;
    }

    @Override
    public Result<List<Source>> listSources() {
        final String fileName = path + "/" + SOURCES_FILE;
        final File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            try {
                return Result.ok(mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Source.class)));
            } catch (IOException e) {
                LOGGER.error("Error reading file {}", fileName, e);
                return Result.error(e);
            }
        }
        return Result.ok(List.of());
    }

    public static Stream<String> getLastLines(int lines, String filePath) throws IOException {
        Deque<String> last100Lines = new ArrayDeque<>(lines);
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (last100Lines.size() == lines) {
                    last100Lines.removeFirst();
                }
                last100Lines.addLast(line);
            }
        }
        return last100Lines.stream();
    }

    @Override
    public Result<List<PublishedItem>> lastPublishedItem(String sourceId) {
        final String fileName = path + "/" + sourceId + ".ndjson";
        final File file = new File(fileName);
        if (file.exists() && file.isFile()) {
            try {
                var lastLines = getLastLines(100, fileName)
                        .map(this::parsePublishedItem)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
                return Result.ok(lastLines);
            } catch (IOException e) {
                LOGGER.error("Error reading file {}", fileName, e);
                return Result.error(e);
            }
        }
        return Result.ok(List.of());
    }

    private Optional<PublishedItem> parsePublishedItem(String json) {
        try {
            return Optional.of(mapper.readValue(json, PublishedItem.class));
        } catch (IOException e) {
            LOGGER.error("Error parsing json {}", json, e);
        }
        return Optional.empty();
    }

    @Override
    public void savePublishedItem(String sourceId, PublishedItem item) {
        final String fileName = path + "/" + sourceId + ".ndjson";
        try {
            Files.writeString(
                    Paths.get(fileName),
                    mapper.writeValueAsString(item) + "\n",
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);
        } catch (IOException e) {
            LOGGER.error("Error writing file {}", fileName, e);
        }
    }
}
