package com.github.romualdrousseau.shuju.json.processing;

import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class JSONProcessingArray implements JSONArray {
    protected processing.data.JSONArray ja;

    public JSONProcessingArray(processing.data.JSONArray ja) {
        this.ja = ja;
    }

    public int size() {
        return this.ja.size();
    }

    public Object get(int i) {
        return this.ja.get(i);
    }

    public int getInt(int i) {
        return this.ja.getInt(i);
    }

    public void setInt(int k, int n) {
        this.ja.setInt(k, n);
    }

    public float getFloat(int k) {
        return this.ja.getFloat(k);
    }

    public void setFloat(int k, float f) {
        this.ja.setFloat(k, f);
    }

    public String getString(int k) {
        return this.ja.getString(k);
    }

    public void setString(int k, String s) {
        this.ja.setString(k, s);
    }

    public JSONArray getJSONArray(int k) {
        return new JSONProcessingArray(this.ja.getJSONArray(k));
    }

    public void setJSONArray(int k, JSONArray a) {
        this.ja.setJSONArray(k, ((JSONProcessingArray) a).ja);
    }

    public JSONObject getJSONObject(int k) {
        return new JSONProcessingObject(this.ja.getJSONObject(k));
    }

    public void setJSONObject(int k, JSONObject o) {
        this.ja.setJSONObject(k, ((JSONProcessingObject) o).jo);
    }

    public void append(int i) {
        this.ja.append(i);
    }

    public void append(float f) {
        this.ja.append(f);
    }

    public void append(String s) {
        this.ja.append(s);
    }

    public void append(JSONArray a) {
        this.ja.append(((JSONProcessingArray) a).ja);
    }

    public void append(JSONObject o) {
        this.ja.append(((JSONProcessingObject) o).jo);
    }

    public void remove(int i) {
        this.ja.remove(i);
    }
}
