package com.github.romualdrousseau.shuju.json;

public interface JSONObject {
    int getInt(String s);

    void setInt(String s, int n);

    float getFloat(String s);

    void setFloat(String s, float f);

    JSONArray getJSONArray(String s);

    void setJSONArray(String s, JSONArray a);

    JSONObject getJSONObject(String s);

    void setJSONObject(String s, JSONObject o);
}
