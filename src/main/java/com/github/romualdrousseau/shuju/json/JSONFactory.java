package com.github.romualdrousseau.shuju.json;

import java.nio.file.Path;

public interface JSONFactory {

    JSONArray newArray();

    JSONArray parseArray(String data);

    JSONArray parseArray(Object object);

    JSONArray loadArray(Path filePath);

    void saveArray(JSONArray a, Path filePath);

    JSONObject newObject();

    JSONObject parseObject(String data);

    JSONObject parseObject(Object object);

    JSONObject loadObject(Path filePath);

    void saveObject(JSONObject o, Path filePath);
}
