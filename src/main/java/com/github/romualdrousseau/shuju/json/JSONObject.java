package com.github.romualdrousseau.shuju.json;

public interface JSONObject {
    int getInt(String k);

    void setInt(String k, int n);

    float getFloat(String k);

    void setFloat(String k, float f);

    String getString(String k);

    void setString(String k, String s);

    JSONArray getJSONArray(String k);

    void setJSONArray(String k, JSONArray a);

    JSONObject getJSONObject(String k);

    void setJSONObject(String k, JSONObject o);
}
