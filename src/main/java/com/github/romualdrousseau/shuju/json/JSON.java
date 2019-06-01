package com.github.romualdrousseau.shuju.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.github.romualdrousseau.shuju.json.jackson.JSONJacksonFactory;
import com.github.romualdrousseau.shuju.util.StringUtility;

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
            if (c != StringUtility.BOM_CHAR) {
                reader.reset();
            }

            c = reader.read();
            return c == '{';

        } catch (Exception x) {
            throw new RuntimeException("Error opening " + filePath);
        }
    }
}
