package com.github.romualdrousseau.shuju.json;

public interface JSONFactory {
    JSONObject newJSONObject();

    JSONArray newJSONArray();

    JSONObject loadJSONObject(String filePath);

    JSONArray loadJSONArray(String filePath);

    void saveJSONObject(String filePath, JSONObject o);

    void saveJSONArray(String filePath, JSONArray a);
}
