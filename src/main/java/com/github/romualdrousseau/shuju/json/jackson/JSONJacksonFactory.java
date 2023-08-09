package com.github.romualdrousseau.shuju.json.jackson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.io.FileInputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONFactory;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class JSONJacksonFactory implements JSONFactory {
    private final ObjectMapper mapper;

    public JSONJacksonFactory() {
        this.mapper = new ObjectMapper();
        final StreamReadConstraints streamReadConstraints = StreamReadConstraints
            .builder()
            .maxStringLength(Integer.MAX_VALUE)
            .build();
        this.mapper.getFactory().setStreamReadConstraints(streamReadConstraints);
    }

    public JSONArray newArray() {
        return new JSONJacksonArray(this.mapper, this.mapper.createArrayNode());
    }

    public JSONArray parseArray(final String data) {
        try {
            return new JSONJacksonArray(this.mapper, this.mapper.readTree(data));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONArray parseArray(final Object object) {
        return new JSONJacksonArray(this.mapper, (JsonNode) object);
    }

    public JSONArray loadArray(final Path filePath) {
        try (BufferedReader reader = this.createReader(filePath)) {
            return new JSONJacksonArray(this.mapper, this.mapper.readTree(reader));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveArray(final JSONArray a, final Path filePath) {
        try {
            mapper.writeValue(filePath.toFile(), ((JSONJacksonArray) a).arrayNode);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject newObject() {
        return new JSONJacksonObject(this.mapper, this.mapper.createObjectNode());
    }

    public JSONObject parseObject(final String data) {
        try {
            return new JSONJacksonObject(this.mapper, this.mapper.readTree(data));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject parseObject(final Object object) {
        return new JSONJacksonObject(this.mapper, (JsonNode) object);
    }

    public JSONObject loadObject(final Path filePath) {
        try (BufferedReader reader = this.createReader(filePath)) {
            return new JSONJacksonObject(this.mapper, this.mapper.readTree(reader));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveObject(final JSONObject o, final Path filePath) {
        try {
            mapper.writeValue(filePath.toFile(), ((JSONJacksonObject) o).objectNode);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedReader createReader(final Path filePath) throws IOException {
        final BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8));

        // consume the Unicode BOM (byte order marker) if present
        reader.mark(1);
        final int c = reader.read();
        // if not the BOM, back up to the beginning again
        if (c != '\uFEFF') {
            reader.reset();
        }

        return reader;
    }
}
