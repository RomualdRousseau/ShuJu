package com.github.romualdrousseau.shuju.json;

public class JSON {
    private static JSONFactory Factory;

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

    public static void saveJSONObject(JSONObject o, String filePath) {
        JSON.Factory.saveJSONObject(o, filePath);
    }

    public static void saveJSONArray(JSONArray a, String filePath) {
        JSON.Factory.saveJSONArray(a, filePath);
    }
}
