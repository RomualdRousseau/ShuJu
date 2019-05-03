package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Vector;

public class StringTypes {
    private ArrayList<String> types = new ArrayList<String>();
    private int maxVectorSize = 0;

    public StringTypes(int maxVectorSize) {
        this.maxVectorSize = maxVectorSize;
    }

    public StringTypes(String[] types) {
        this.maxVectorSize = types.length;
        this.types.addAll(Arrays.asList(types));
    }

    public StringTypes(JSONObject json) {
        this.maxVectorSize = json.getInt("maxVectorSize");

        JSONArray jsonTypes = json.getJSONArray("types");
        for(int i = 0; i < jsonTypes.size(); i++) {
            String s = jsonTypes.getString(i);
            this.types.add(s);
        }
    }

    public List<String> types() {
        return this.types;
    }

    public int size() {
        return this.types.size();
    }

    public String value(int i) {
        return this.types.get(i);
    }

    public int ordinal(String type) {
        return this.types.indexOf(type);
    }

    public void registerType(String type) {
        assert this.types.indexOf(type) == -1;
        this.types.add(type);
    }

    public Vector word2vec(String w) {
        Vector result = new Vector(this.maxVectorSize);

        if(w == null) {
            return result;
        }

        result.oneHot(this.ordinal(w));

        return result;
    }

    public JSONObject toJSON() {
        JSONArray jsonTypes = JSON.newJSONArray();
        for (String t : this.types) {
            jsonTypes.append(t);
        }

        JSONObject json = JSON.newJSONObject();
        json.setInt("maxVectorSize", this.maxVectorSize);
        json.setJSONArray("types", jsonTypes);
        return json;
    }
}
