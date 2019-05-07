package com.github.romualdrousseau.shuju.json.simple;

import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class JSONSimpleArray implements JSONArray {
    protected org.json.simple.JSONArray ja;

    public JSONSimpleArray(org.json.simple.JSONArray ja) {
        this.ja = ja;
    }

    public int size() {
        return this.ja.size();
    }

    public int getInt(int k) {
        return ((Long) this.ja.get(k)).intValue();
    }

    @SuppressWarnings("unchecked")
    public void setInt(int k, int n) {
        this.ja.set(k, Long.valueOf(n));
    }

    public float getFloat(int k) {
        return ((Double) this.ja.get(k)).floatValue();
    }

    @SuppressWarnings("unchecked")
    public void setFloat(int k, float f) {
        this.ja.set(k, Double.valueOf(f));
    }

    public String getString(int k) {
        return (String) this.ja.get(k);
    }

    @SuppressWarnings("unchecked")
    public void setString(int k, String s) {
        this.ja.set(k, s);
    }

    public JSONArray getJSONArray(int k) {
        return new JSONSimpleArray((org.json.simple.JSONArray) this.ja.get(k));
    }

    @SuppressWarnings("unchecked")
    public void setJSONArray(int k, JSONArray a) {
        this.ja.set(k, ((JSONSimpleArray) a).ja);
    }

    public JSONObject getJSONObject(int k) {
        return new JSONSimpleObject((org.json.simple.JSONObject) this.ja.get(k));
    }

    @SuppressWarnings("unchecked")
    public void setJSONObject(int k, JSONObject o) {
        this.ja.set(k, ((JSONSimpleObject) o).jo);
    }

    @SuppressWarnings("unchecked")
    public void append(int i) {
        this.ja.add(i);
    }

    @SuppressWarnings("unchecked")
    public void append(float f) {
        this.ja.add(f);
    }

    @SuppressWarnings("unchecked")
    public void append(String s) {
        this.ja.add(s);
    }

    @SuppressWarnings("unchecked")
    public void append(JSONArray a) {
        this.ja.add(((JSONSimpleArray) a).ja);
    }

    @SuppressWarnings("unchecked")
    public void append(JSONObject o) {
        this.ja.add(((JSONSimpleObject) o).jo);
    }
}
