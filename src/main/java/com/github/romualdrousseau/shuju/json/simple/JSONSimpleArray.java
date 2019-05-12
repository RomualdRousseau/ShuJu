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

    public Object get(int i) {
        return this.ja.get(i);
    }

    public int getInt(int i) {
        return ((Long) this.ja.get(i)).intValue();
    }

    @SuppressWarnings("unchecked")
    public void setInt(int i, int n) {
        this.ja.set(i, Long.valueOf(n));
    }

    public float getFloat(int i) {
        return ((Double) this.ja.get(i)).floatValue();
    }

    @SuppressWarnings("unchecked")
    public void setFloat(int i, float f) {
        this.ja.set(i, Double.valueOf(f));
    }

    public String getString(int i) {
        return (String) this.ja.get(i);
    }

    @SuppressWarnings("unchecked")
    public void setString(int i, String s) {
        this.ja.set(i, s);
    }

    public JSONArray getJSONArray(int i) {
        return new JSONSimpleArray((org.json.simple.JSONArray) this.ja.get(i));
    }

    @SuppressWarnings("unchecked")
    public void setJSONArray(int i, JSONArray a) {
        this.ja.set(i, ((JSONSimpleArray) a).ja);
    }

    public JSONObject getJSONObject(int i) {
        return new JSONSimpleObject((org.json.simple.JSONObject) this.ja.get(i));
    }

    @SuppressWarnings("unchecked")
    public void setJSONObject(int i, JSONObject o) {
        this.ja.set(i, ((JSONSimpleObject) o).jo);
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

    public void remove(int i) {
        this.ja.remove(i);
    }
}
