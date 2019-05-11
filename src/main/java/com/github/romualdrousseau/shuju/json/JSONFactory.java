package com.github.romualdrousseau.shuju.json;

public interface JSONFactory {
    JSONObject newJSONObject();

    JSONArray newJSONArray();

    JSONObject loadJSONObject(String filePath);

    JSONArray loadJSONArray(String filePath);

    JSONObject parseJSONObject(String data);

    JSONArray parseJSONArray(String data);

    void saveJSONObject(JSONObject o, String filePath);

    void saveJSONArray(JSONArray a, String filePath);
}
