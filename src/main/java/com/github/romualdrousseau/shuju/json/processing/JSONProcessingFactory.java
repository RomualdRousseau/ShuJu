package com.github.romualdrousseau.shuju.json.processing;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;

import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONFactory;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class JSONProcessingFactory implements JSONFactory {
    public JSONProcessingFactory() {
    }

    public JSONArray newJSONArray() {
        return new JSONProcessingArray(new processing.data.JSONArray());
    }

    public JSONObject newJSONObject() {
        return new JSONProcessingObject(new processing.data.JSONObject());
    }

    public JSONObject loadJSONObject(String filePath) {
        try (BufferedReader reader = createReader(filePath)) {
            return new JSONProcessingObject(new processing.data.JSONObject(reader));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONArray loadJSONArray(String filePath) {
        try (BufferedReader reader = createReader(filePath)) {
            return new JSONProcessingArray(new processing.data.JSONArray(reader));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject parseJSONObject(String data) {
        return new JSONProcessingObject(new processing.data.JSONObject(new StringReader(data)));
    }

    public JSONArray parseJSONArray(String data) {
        return new JSONProcessingArray(new processing.data.JSONArray(new StringReader(data)));
    }

    public void saveJSONObject(JSONObject o, String filePath) {
        ((JSONProcessingObject) o).jo.save(new File(filePath), null);
    }

    public void saveJSONArray(JSONArray a, String filePath) {
        ((JSONProcessingArray) a).ja.save(new File(filePath), null);
    }

    private BufferedReader createReader(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(new File(filePath)), StandardCharsets.UTF_8));

        // consume the Unicode BOM (byte order marker) if present
        reader.mark(1);
        int c = reader.read();
        // if not the BOM, back up to the beginning again
        if (c != '\uFEFF') {
            reader.reset();
        }

        return reader;
    }
}
