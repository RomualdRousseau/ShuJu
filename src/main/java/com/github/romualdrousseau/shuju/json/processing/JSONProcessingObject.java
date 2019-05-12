package com.github.romualdrousseau.shuju.json.processing;

import java.util.Set;

import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class JSONProcessingObject implements JSONObject {
    protected processing.data.JSONObject jo;

    public JSONProcessingObject(processing.data.JSONObject jo) {
        this.jo = jo;
    }

    @SuppressWarnings("unchecked")
    public Set<String> keys() {
        return this.jo.keys();
    }

    public Object get(String k) {
        return this.jo.get(k);
    }

    public int getInt(String k) {
        return this.jo.getInt(k);
    }

    public void setInt(String k, int n) {
        this.jo.setInt(k, n);
    }

    public float getFloat(String k) {
        return this.jo.getFloat(k);
    }

    public void setFloat(String k, float f) {
        this.jo.setFloat(k, f);
    }

    public String getString(String k) {
        return this.jo.getString(k);
    }

    public void setString(String k, String s) {
        this.jo.setString(k, s);
    }

    public JSONArray getJSONArray(String k) {
        return new JSONProcessingArray(this.jo.getJSONArray(k));
    }

    public void setJSONArray(String k, JSONArray a) {
        this.jo.setJSONArray(k, ((JSONProcessingArray) a).ja);
    }

    public JSONObject getJSONObject(String k) {
        return new JSONProcessingObject(this.jo.getJSONObject(k));
    }

    public void setJSONObject(String k, JSONObject o) {
        this.jo.setJSONObject(k, ((JSONProcessingObject) o).jo);
    }
}
