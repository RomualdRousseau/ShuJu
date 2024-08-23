package com.github.romualdrousseau.shuju.yaml;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.reflections.Reflections;

public class YAML {
    public final static String PACKAGE_LOADER_PREFIX = "com.github.romualdrousseau.shuju.yaml";

    private static YAMLFactory Factory;
    static {
        final Reflections reflections = new Reflections(PACKAGE_LOADER_PREFIX);
        YAML.Factory = reflections.getSubTypesOf(YAMLFactory.class).stream()
                .map(YAML::newFactoryInstance)
                .findFirst()
                .get();
    }

    private static <T> YAMLFactory newFactoryInstance(Class<T> clazz) {
        try {
            return (YAMLFactory) clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static YAMLArray newArray() {
        return YAML.Factory.newArray();
    }

    public static YAMLArray arrayOf(String data) {
        return YAML.Factory.parseArray(data);
    }

    public static YAMLArray arrayOf(Object object) {
        return YAML.Factory.parseArray(object);
    }

    public static <T> YAMLArray arrayOf(final List<T> l) {
        final YAMLArray array = YAML.newArray();
        l.forEach(s -> array.append(s));
        return array;
    }

    public static <T> YAMLArray arrayOf(final Stream<T> l) {
        final YAMLArray array = YAML.newArray();
        l.forEach(s -> array.append(s));
        return array;
    }

    public static <T> YAMLArray arrayOf(final Map<String, T> m) {
        final YAMLArray array = YAML.newArray();
        m.forEach((k, v) -> {
            YAMLObject pair = YAML.newObject();
            pair.set("key", k);
            pair.set("value", v);
            array.append(pair);
        });
        return array;
    }

    public static YAMLArray loadArray(Path filePath) {
        return YAML.Factory.loadArray(filePath);
    }

    public static void saveArray(YAMLArray a, Path filePath) {
        YAML.Factory.saveArray(a, filePath, false);
    }

    public static void saveArray(YAMLArray a, Path filePath, final boolean pretty) {
        YAML.Factory.saveArray(a, filePath, pretty);
    }

    public static YAMLObject newObject() {
        return YAML.Factory.newObject();
    }

    public static YAMLObject objectOf(String data) {
        return YAML.Factory.parseObject(data);
    }

    public static YAMLObject objectOf(Object object) {
        return YAML.Factory.parseObject(object);
    }

    public static <T> YAMLObject objectOf(final Map<String, T> m) {
        final YAMLObject object = YAML.newObject();
        m.forEach((k, v) -> object.set(k, v));
        return object;
    }

    public static YAMLObject loadObject(Path filePath) {
        return YAML.Factory.loadObject(filePath);
    }

    public static void saveObject(YAMLObject o, Path filePath) {
        YAML.Factory.saveObject(o, filePath, false);
    }

    public static void saveObject(YAMLObject o, Path filePath, final boolean pretty) {
        YAML.Factory.saveObject(o, filePath, pretty);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> query(final Object a, final String q) {
        Object curr = a;
        for(String token: Arrays.asList(q.split("\\."))) {
            if (curr instanceof YAMLArray) {
                int i = Integer.parseInt(token);
                curr = ((YAMLArray) curr).get(i).orElse(null);
            } else if (curr instanceof YAMLObject) {
                curr = ((YAMLObject) curr).get(token).orElse(null);
            } else {
                curr = null;
            }
        }
        return Optional.ofNullable((T) curr);
    }

    public static <T> Stream<T> streamOf(final Object a, final String q) {
        return YAML.<T>query(a, q)
                .filter(o -> o instanceof YAMLArray)
                .map(o -> YAML.<T>streamOf((YAMLArray) o))
                .orElse(Stream.empty());
    }

    public static <T> Stream<T> streamOf(final YAMLArray a) {
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
                        return a.<T>get(idx++).get();
                    }
                };
            }
        };
        return StreamSupport.stream(it.spliterator(), false);
    }
}
