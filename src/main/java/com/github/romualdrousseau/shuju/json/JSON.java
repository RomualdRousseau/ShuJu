package com.github.romualdrousseau.shuju.json;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class JSON {
    public final static String PACKAGE_LOADER_PREFIX = "com.github.romualdrousseau.shuju.json";

    private static JSONFactory Factory;
    static {
        final Reflections reflections = new Reflections(PACKAGE_LOADER_PREFIX, new SubTypesScanner(false));
        JSON.Factory = reflections.getSubTypesOf(JSONFactory.class).stream()
                .map(JSON::newFactoryInstance)
                .findFirst()
                .get();
    }

    private static <T> JSONFactory newFactoryInstance(Class<T> clazz) {
        try {
            return (JSONFactory) clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONArray newArray() {
        return JSON.Factory.newArray();
    }

    public static JSONArray arrayOf(String data) {
        return JSON.Factory.parseArray(data);
    }

    public static JSONArray arrayOf(Object object) {
        return JSON.Factory.parseArray(object);
    }

    public static <T> JSONArray arrayOf(final List<T> l) {
        final JSONArray array = JSON.newArray();
        l.forEach(s -> array.append(s));
        return array;
    }

    public static <T> JSONArray arrayOf(final Map<String, T> m) {
        final JSONArray array = JSON.newArray();
        m.forEach((k, v) -> {
            JSONObject pair = JSON.newObject();
            pair.set("key", k);
            pair.set("value", v);
            array.append(pair);
        });
        return array;
    }

    public static JSONArray loadArray(Path filePath) {
        return JSON.Factory.loadArray(filePath);
    }

    public static void saveArray(JSONArray a, Path filePath) {
        JSON.Factory.saveArray(a, filePath);
    }

    public static JSONObject newObject() {
        return JSON.Factory.newObject();
    }

    public static JSONObject objectOf(String data) {
        return JSON.Factory.parseObject(data);
    }

    public static JSONObject objectOf(Object object) {
        return JSON.Factory.parseObject(object);
    }

    public static <T> JSONObject objectOf(final Map<String, T> m) {
        final JSONObject object = JSON.newObject();
        m.forEach((k, v) -> object.set(k, v));
        return object;
    }

    public static JSONObject loadObject(Path filePath) {
        return JSON.Factory.loadObject(filePath);
    }

    public static void saveObject(JSONObject o, Path filePath) {
        JSON.Factory.saveObject(o, filePath);
    }

    public static <T> Stream<T> streamOf(final JSONArray a) {
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

    public static <T> Stream<T> streamOf(final Object a, final String q) {
        T o = JSON.query(a, q);
        if (o instanceof JSONArray) {
            return JSON.streamOf((JSONArray) o);
        } else {
            return Stream.empty();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T query(final Object a, final String q) {
        Object curr = a;
        for(String token: Arrays.asList(q.split("\\."))) {
            if (curr instanceof JSONArray) {
                int i = Integer.parseInt(token);
                curr = ((JSONArray) curr).get(i);
            } else if (curr instanceof JSONObject) {
                curr = ((JSONObject) curr).get(token).get();
            }
        }
        return (T) curr;
    }
}
