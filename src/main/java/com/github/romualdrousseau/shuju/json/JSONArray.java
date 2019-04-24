package com.github.romualdrousseau.shuju.json;

public interface JSONArray {
    int size();

    int getInt(int s);

    void setInt(int i, int n);

    float getFloat(int i);

    void setFloat(int i, float f);

    JSONArray getJSONArray(int i);

    void setJSONArray(int i, JSONArray f);

    JSONObject getJSONObject(int i);

    void setJSONObject(int i, JSONObject o);

    void append(int i );

    void append(float f);

    void append(JSONObject o);
}
