package com.github.romualdrousseau.shuju.json.processing;

import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONFactory;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class JSONProcessingFactory implements JSONFactory {
    private processing.core.PApplet applet;

    public JSONProcessingFactory(processing.core.PApplet applet) {
        this.applet = applet;
    }

    public JSONArray newJSONArray() {
        return new JSONProcessingArray(new processing.data.JSONArray());
    }

    public JSONObject newJSONObject() {
        return new JSONProcessingObject(new processing.data.JSONObject());
    }

    public JSONObject loadJSONObject(String filePath) {
        return new JSONProcessingObject(this.applet.loadJSONObject(filePath));
    }

    public JSONArray loadJSONArray(String filePath) {
        return new JSONProcessingArray(this.applet.loadJSONArray(filePath));
    }

    public void saveJSONObject(JSONObject o, String filePath) {
        this.applet.saveJSONObject(((JSONProcessingObject) o).jo, filePath);
    }

    public void saveJSONArray(JSONArray a, String filePath) {
        this.applet.saveJSONArray(((JSONProcessingArray) a).ja, filePath);
    }
}
