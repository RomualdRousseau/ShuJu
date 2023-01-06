package com.github.romualdrousseau.shuju.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    public static <T> JSONArray parseJSONArray(final List<T> l) {
        final JSONArray array = JSON.newJSONArray();
        l.forEach(s -> array.append(s));
        return array;   
    }

    public static <T> JSONObject parseJSONObject(final Map<String, T> m) {
        final JSONObject object = JSON.newJSONObject();
        m.forEach((k, v) -> object.set(k, v));
        return object;
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

    @SuppressWarnings("unchecked")
    public static <T> T query(final Object a, final String q) {
        final List<String> t = Arrays.asList(q.split("\\."));
        Object curr = a;
        int state = 0;
        while(state < t.size()) {
            if (curr instanceof JSONArray) {
                int i = Integer.parseInt(t.get(state));
                curr = ((JSONArray) curr).get(i);
            }
            else if (curr instanceof JSONObject) {
                curr = ((JSONObject) curr).get(t.get(state)).get();
            }
            state++;
        }
        return (T) curr;
    }

    public static <T> Stream<T> Stream(final JSONArray a) {
        Iterable<T> it = new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {
                    private int idx = 0;

                    @Override
                    public boolean hasNext() {
                        return idx < a.size();
                    }

                    @Override
                    public T next() {
                        return a.get(idx++);
                    } 
                };
            }
        };
        return StreamSupport.stream(it.spliterator(), false);
    }

    public static <T> Stream<T> queryAsStream(final Object a, final String q) {
        T o = JSON.query(a, q);
        if (o instanceof JSONArray) {
            return JSON.Stream((JSONArray) o);
        } else {
            return Stream.empty();
        }
    }
}
