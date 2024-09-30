package com.hogemann.bsky2rss.bot.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hogemann.bsky2rss.bot.model.PublishedItem;
import com.hogemann.bsky2rss.bot.model.Source;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class JsonFileRepository implements Rss2BskyRepository {

    private final static Logger LOGGER = Logger.getLogger(JsonFileRepository.class.getName());
    private final static String SOURCES_FILE = "sources.json";
    private final ObjectMapper mapper;
    private final String path;

    @Inject
    public JsonFileRepository(ObjectMapper mapper, String path) {
        this.mapper = mapper;
        this.path = path;
    }

    @Override
    public List<Source> listSources() {
        final String fileName = path + "/" + SOURCES_FILE;
        final File file = new File(fileName);
        if(file.exists() && file.isFile()) {
            try {
                return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, Source.class));
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error reading file " + fileName, e);
            }
        }
        return List.of();
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
    public List<PublishedItem> lastPublishedItem(UUID sourceId) {
        final String fileName = path + "/" + sourceId + ".json";
        final File file = new File(fileName);
        if(file.exists() && file.isFile()) {
            try {
                return getLastLines(100, fileName)
                        .map(this::parsePublishedItem)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .toList();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error reading file " + fileName, e);
            }
        }
        return List.of();
    }

    private Optional<PublishedItem> parsePublishedItem(String json) {
        try {
            return Optional.of(mapper.readValue(json, PublishedItem.class));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error parsing json " + json, e);
        }
        return Optional.empty();
    }

    @Override
    public void savePublishedItem(UUID sourceId, PublishedItem item) {
        final String fileName = path + "/" + sourceId + ".json";
        try {
            Files.writeString(Paths.get(fileName), mapper.writeValueAsString(item) + "\n");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing file " + fileName, e);
        }
    }
}
