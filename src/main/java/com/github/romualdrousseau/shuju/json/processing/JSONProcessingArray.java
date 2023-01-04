package com.github.romualdrousseau.shuju.json.processing;

import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class JSONProcessingArray implements JSONArray {
    protected processing.data.JSONArray ja;

    public JSONProcessingArray(processing.data.JSONArray ja) {
        this.ja = ja;
    }

    public int size() {
        return (this.ja == null) ? 0 : this.ja.size();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(int i) {
        Object o = this.ja.get(i);
        if (o instanceof processing.data.JSONObject) {
            return (T) new JSONProcessingObject((processing.data.JSONObject) o);
        } else if (o instanceof processing.data.JSONArray) {
            return (T) new JSONProcessingArray((processing.data.JSONArray) o);
        } else {
            return (T) o;
        }
    }

    public void set(int i, Object o) {
        if (o instanceof JSONObject) {
            this.ja.setJSONObject(i, (processing.data.JSONObject) o);
        } else if (o instanceof JSONArray) {
            this.ja.setJSONArray(i, (processing.data.JSONArray) o);
        } else if (o instanceof Integer) {
            this.ja.setInt(i, (Integer) o);
        } else if (o instanceof Float) {
            this.ja.setFloat(i, (Float) o);
        } else {
            this.ja.setString(i, o.toString());
        }
    }

    public int getInt(int i) {
        return this.ja.getInt(i);
    }

    public void setInt(int i, int n) {
        this.ja.setInt(i, n);
    }

    public float getFloat(int i) {
        return this.ja.getFloat(i);
    }

    public void setFloat(int i, float f) {
        this.ja.setFloat(i, f);
    }

    public String getString(int i) {
        return this.ja.getString(i);
    }

    public void setString(int i, String s) {
        this.ja.setString(i, s);
    }

    public JSONArray getJSONArray(int i) {
        return new JSONProcessingArray(this.ja.getJSONArray(i));
    }

    public void setJSONArray(int i, JSONArray a) {
        this.ja.setJSONArray(i, ((JSONProcessingArray) a).ja);
    }

    public JSONObject getJSONObject(int i) {
        return new JSONProcessingObject(this.ja.getJSONObject(i));
    }

    public void setJSONObject(int i, JSONObject o) {
        this.ja.setJSONObject(i, ((JSONProcessingObject) o).jo);
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

    public void append(Object o) {
        if (o instanceof JSONObject) {
            this.ja.append(((JSONProcessingObject) o).jo);
        } else if (o instanceof JSONArray) {
            this.ja.append(((JSONProcessingArray) o).ja);
        } else if (o instanceof Integer) {
            this.ja.append((Integer) o);
        } else if (o instanceof Float) {
            this.ja.append((Float) o);
        } else {
            this.ja.append(o.toString());
        }
    }

    public void remove(int i) {
        this.ja.remove(i);
    }

    @Override
    public String toString() {
        return this.ja.toString();
    }
}
