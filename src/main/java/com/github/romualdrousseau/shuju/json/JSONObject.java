package com.github.romualdrousseau.shuju.json;

import java.util.Optional;

public interface JSONObject {
    Iterable<String> keys();

    <T> Optional<T> get(String k);

    void set(String k, Object o);

    int getInt(String k);

    void setInt(String k, int n);

    float getFloat(String k);

    void setFloat(String k, float f);

    String getString(String k);

    void setString(String k, String s);

    JSONArray getArray(String k);

    void setArray(String k, JSONArray a);

    JSONObject getObject(String k);

    void setObject(String k, JSONObject o);

    String toString();
}
