package com.github.romualdrousseau.shuju.json.jackson;

import java.util.Iterator;
import java.util.Optional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class JSONJacksonObject implements JSONObject {
    private final ObjectMapper mapper;
    protected ObjectNode objectNode;

    public JSONJacksonObject(final ObjectMapper mapper, final JsonNode node) {
        this.mapper = mapper;
        if (node == null) {
            this.objectNode = mapper.createObjectNode();
        } else {
            this.objectNode = (ObjectNode) node;
        }
    }

    public Iterable<String> keys() {
        return new Iterable<String>() {
            @Override
            public Iterator<String> iterator()
            {
                return JSONJacksonObject.this.objectNode.fieldNames();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(final String k) {
        final JsonNode node = this.objectNode.get(k);
        if (node == null) {
            return Optional.empty();
        }
        if (node.isObject()) {
            return Optional.of((T) new JSONJacksonObject(this.mapper, node));
        } else if (node.isArray()) {
            return Optional.of((T) new JSONJacksonArray(this.mapper, node));
        } else if (node.isInt()) {
            return Optional.of((T) Integer.valueOf(node.intValue()));
        } else if (node.isFloat()) {
            return Optional.of((T) Float.valueOf(node.floatValue()));
        } else {
            return Optional.of((T) node.textValue());
        }
    }

    public void set(final String k, final Object o) {
        if (o instanceof JSONObject) {
            this.objectNode.set(k, (JsonNode) ((JSONJacksonObject) o).objectNode);
        } else if (o instanceof JSONArray) {
            this.objectNode.set(k, (JsonNode) ((JSONJacksonArray) o).arrayNode);
        } else {
            this.objectNode.set(k, this.mapper.convertValue(o, JsonNode.class));
        }
    }

    public int getInt(final String k) {
        return Optional.ofNullable(this.objectNode.get(k)).map(v -> v.intValue()).orElse(0);
    }

    public void setInt(final String k, final int n) {
        this.objectNode.put(k, n);
    }

    public float getFloat(final String k) {
        return Optional.ofNullable(this.objectNode.get(k)).map(v -> v.floatValue()).orElse(0.0f);
    }

    public void setFloat(final String k, final float f) {
        this.objectNode.put(k, f);
    }

    public String getString(final String k) {
        return Optional.ofNullable(this.objectNode.get(k)).map(v -> v.textValue()).orElse(null);
    }

    public void setString(final String k, final String s) {
        this.objectNode.put(k, s);
    }

    public JSONArray getJSONArray(final String k) {
        return new JSONJacksonArray(this.mapper, this.objectNode.get(k));
    }

    public void setJSONArray(final String k, final JSONArray a) {
        this.objectNode.set(k, ((JSONJacksonArray) a).arrayNode);
    }

    public JSONObject getJSONObject(final String k) {
        return new JSONJacksonObject(this.mapper, this.objectNode.get(k));
    }

    public void setJSONObject(final String k, final JSONObject o) {
        this.objectNode.set(k, ((JSONJacksonObject) o).objectNode);
    }

    @Override
    public String toString() {
        return this.objectNode.toString();
    }
}
