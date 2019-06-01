package com.github.romualdrousseau.shuju.json.jackson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONFactory;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class JSONJacksonFactory implements JSONFactory {
    private ObjectMapper mapper;

    public JSONJacksonFactory() {
        this.mapper = new ObjectMapper();
    }

    public JSONArray newJSONArray() {
        return new JSONJacksonArray(this.mapper, this.mapper.createArrayNode());
    }

    public JSONObject newJSONObject() {
        return new JSONJacksonObject(this.mapper, this.mapper.createObjectNode());
    }

    public JSONObject loadJSONObject(String filePath) {
        try (BufferedReader reader = createReader(filePath)) {
            return new JSONJacksonObject(this.mapper, this.mapper.readTree(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONArray loadJSONArray(String filePath) {
        try (BufferedReader reader = createReader(filePath)) {
            return new JSONJacksonArray(this.mapper, this.mapper.readTree(reader));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject parseJSONObject(String data) {
        try {
            return new JSONJacksonObject(this.mapper, this.mapper.readTree(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONArray parseJSONArray(String data) {
        try {
            return new JSONJacksonArray(this.mapper, this.mapper.readTree(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject parseJSONObject(Object object) {
        return new JSONJacksonObject(this.mapper, (JsonNode) object);
    }

    public JSONArray parseJSONArray(Object object) {
        return new JSONJacksonArray(this.mapper, (JsonNode) object);
    }

    public void saveJSONObject(JSONObject o, String filePath) {
        try {
            mapper.writeValue(new File(filePath), ((JSONJacksonObject) o).objectNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveJSONArray(JSONArray a, String filePath) {
        try {
            mapper.writeValue(new File(filePath), ((JSONJacksonArray) a).arrayNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedReader createReader(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(filePath)), StandardCharsets.UTF_8));

        // consume the Unicode BOM (byte order marker) if present
        reader.mark(1);
        int c = reader.read();
        // if not the BOM, back up to the beginning again
        if (c != '\uFEFF') {
            reader.reset();
        }

        return reader;
    }
}
