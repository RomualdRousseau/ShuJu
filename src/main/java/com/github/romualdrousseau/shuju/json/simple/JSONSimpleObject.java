package com.github.romualdrousseau.shuju.json.simple;

import java.util.Set;

import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class JSONSimpleObject implements JSONObject {
    protected org.json.simple.JSONObject jo;

    public JSONSimpleObject(org.json.simple.JSONObject jo) {
        this.jo = jo;
    }

    @SuppressWarnings("unchecked")
    public Set<String> keys() {
        return this.jo.keySet();
    }

    public Object get(String k) {
        return this.jo.get(k);
    }

    public int getInt(String k) {
        return ((Long) this.jo.get(k)).intValue();
    }

    @SuppressWarnings("unchecked")
    public void setInt(String k, int n) {
        this.jo.put(k, Long.valueOf(n));
    }

    public float getFloat(String k) {
        return ((Double) this.jo.get(k)).floatValue();
    }

    @SuppressWarnings("unchecked")
    public void setFloat(String k, float f) {
        this.jo.put(k, Double.valueOf(f));
    }

    public String getString(String k) {
        return (String) this.jo.get(k);
    }

    @SuppressWarnings("unchecked")
    public void setString(String k, String s) {
        this.jo.put(k, s);
    }

    public JSONArray getJSONArray(String k) {
        return new JSONSimpleArray((org.json.simple.JSONArray) this.jo.get(k));
    }

    @SuppressWarnings("unchecked")
    public void setJSONArray(String k, JSONArray a) {
        this.jo.put(k, ((JSONSimpleArray) a).ja);
    }

    public JSONObject getJSONObject(String k) {
        return new JSONSimpleObject((org.json.simple.JSONObject) this.jo.get(k));
    }

    @SuppressWarnings("unchecked")
    public void setJSONObject(String k, JSONObject o) {
        this.jo.put(k, ((JSONSimpleObject) o).jo);
    }
}
