
package com.github.romualdrousseau.shuju.json;

public interface JSONArray {
    int size();

    <T> T get(int i);

    void set(int i, Object o);

    int getInt(int s);

    void setInt(int i, int n);

    float getFloat(int i);

    void setFloat(int i, float f);

    String getString(int i);

    void setString(int i, String s);

    JSONArray getArray(int i);

    void setArray(int i, JSONArray f);

    JSONObject getObject(int i);

    void setOject(int i, JSONObject o);

    void append(int i );

    void append(float f);

    void append(String s);

    void append(JSONArray a);

    void append(JSONObject o);

    void append(Object o);

    void remove(int i);
}
