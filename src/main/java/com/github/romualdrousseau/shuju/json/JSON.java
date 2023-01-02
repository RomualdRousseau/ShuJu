package com.github.romualdrousseau.shuju.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.romualdrousseau.shuju.json.jackson.JSONJacksonFactory;
import com.github.romualdrousseau.shuju.util.StringUtils;

public class JSON {
    private static JSONFactory Factory;

    static {
        JSON.Factory = new JSONJacksonFactory();
    }

    public static void setFactory(JSONFactory factory) {
        JSON.Factory = factory;
    }

    public static JSONArray newJSONArray() {
        return JSON.Factory.newJSONArray();
    }

    public static JSONObject newJSONObject() {
        return JSON.Factory.newJSONObject();
    }

    public static JSONObject loadJSONObject(String filePath) {
        return JSON.Factory.loadJSONObject(filePath);
    }

    public static JSONArray loadJSONArray(String filePath) {
        return JSON.Factory.loadJSONArray(filePath);
    }

    public static JSONObject parseJSONObject(String data) {
        return JSON.Factory.parseJSONObject(data);
    }

    public static JSONArray parseJSONArray(String data) {
        return JSON.Factory.parseJSONArray(data);
    }

    public static JSONObject parseJSONObject(Object object) {
        return JSON.Factory.parseJSONObject(object);
    }

    public static JSONArray parseJSONArray(Object object) {
        return JSON.Factory.parseJSONArray(object);
    }

    public static void saveJSONObject(JSONObject o, String filePath) {
        JSON.Factory.saveJSONObject(o, filePath);
    }

    public static void saveJSONArray(JSONArray a, String filePath) {
        JSON.Factory.saveJSONArray(a, filePath);
    }

    public static boolean checkIfJSONObject(String filePath) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(filePath)), StandardCharsets.UTF_8))) {

            // consume the Unicode BOM (byte order marker) if present
            reader.mark(1);
            int c = reader.read();
            // if not the BOM, back up to the beginning again
            if (c != StringUtils.BOM_CHAR) {
                reader.reset();
            }

            c = reader.read();
            return c == '{';

        } catch (Exception x) {
            throw new RuntimeException("Error opening " + filePath);
        }
    }

    public static Stream<JSONObject> StreamJSONObject(final JSONArray a) {
        Iterable<JSONObject> it = new Iterable<JSONObject>() {
            @Override
            public Iterator<JSONObject> iterator() {
                return new Iterator<JSONObject>() {
                    private int idx = 0;

                    @Override
                    public boolean hasNext() {
                        return idx < a.size();
                    }

                    @Override
                    public JSONObject next() {
                        return a.getJSONObject(idx++);
                    } 
                };
            }
        };
        return StreamSupport.stream(it.spliterator(), false);
    }

    public static Stream<String> StreamString(final JSONArray a) {
        Iterable<String> it = new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    private int idx = 0;

                    @Override
                    public boolean hasNext() {
                        return idx < a.size();
                    }

                    @Override
                    public String next() {
                        return a.getString(idx++);
                    } 
                };
            }
        };
        return StreamSupport.stream(it.spliterator(), false);
    }
}
