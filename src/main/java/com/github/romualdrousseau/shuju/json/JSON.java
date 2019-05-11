package com.github.romualdrousseau.shuju.json;

import com.github.romualdrousseau.shuju.json.processing.JSONProcessingFactory;

public class JSON {
    private static JSONFactory Factory;

    static {
        JSON.Factory = new JSONProcessingFactory();
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

    public static void saveJSONObject(JSONObject o, String filePath) {
        JSON.Factory.saveJSONObject(o, filePath);
    }

    public static void saveJSONArray(JSONArray a, String filePath) {
        JSON.Factory.saveJSONArray(a, filePath);
    }
}
