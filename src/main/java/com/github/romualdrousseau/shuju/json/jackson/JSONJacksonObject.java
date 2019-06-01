package com.github.romualdrousseau.shuju.json.jackson;

import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class JSONJacksonObject implements JSONObject {
    private ObjectMapper mapper;
    protected ObjectNode objectNode;

    public JSONJacksonObject(ObjectMapper mapper, JsonNode node) {
        this.mapper = mapper;
        this.objectNode = (ObjectNode) node;
    }

    public Iterable<String> keys() {
        return this.getIterableFromIterator(this.objectNode.fieldNames());
    }

    public Object get(String k) {
        JsonNode node = this.objectNode.get(k);
        if (node.isObject()) {
            return new JSONJacksonObject(this.mapper, node);
        } else if (node.isArray()) {
            return new JSONJacksonArray(this.mapper, node);
        } else if (node.isInt()) {
            return node.intValue();
        } else if (node.isFloat()) {
            return node.floatValue();
        } else {
            return node.textValue();
        }
    }

    public void set(String k, Object o) {
        if (o instanceof JSONObject) {
            this.objectNode.set(k, (JsonNode) o);
        } else if (o instanceof JSONArray) {
            this.objectNode.set(k, (JsonNode) o);
        } else {
            this.objectNode.set(k, this.mapper.convertValue(o, JsonNode.class));
        }
    }

    public int getInt(String k) {
        return this.objectNode.get(k).intValue();
    }

    public void setInt(String k, int n) {
        this.objectNode.set(k, this.mapper.convertValue(n, JsonNode.class));
    }

    public float getFloat(String k) {
        return this.objectNode.get(k).floatValue();
    }

    public void setFloat(String k, float f) {
        this.objectNode.set(k, this.mapper.convertValue(f, JsonNode.class));
    }

    public String getString(String k) {
        return this.objectNode.get(k).textValue();
    }

    public void setString(String k, String s) {
        this.objectNode.set(k, this.mapper.convertValue(s, JsonNode.class));
    }

    public JSONArray getJSONArray(String k) {
        return new JSONJacksonArray(this.mapper, this.objectNode.get(k));
    }

    public void setJSONArray(String k, JSONArray a) {
        this.objectNode.set(k, ((JSONJacksonArray) a).arrayNode);
    }

    public JSONObject getJSONObject(String k) {
        return new JSONJacksonObject(this.mapper, this.objectNode.get(k));
    }

    public void setJSONObject(String k, JSONObject o) {
        this.objectNode.set(k, ((JSONJacksonObject) o).objectNode);
    }

    @Override
    public String toString() {
        return this.objectNode.toString();
    }

    private <T> Iterable<T> getIterableFromIterator(Iterator<T> iterator)
    {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator()
            {
                return iterator;
            }
        };
    }
}
