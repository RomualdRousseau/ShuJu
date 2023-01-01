package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor1D;
import com.github.romualdrousseau.shuju.util.StringUtils;

public class StringList implements BaseList {
    private ArrayList<String> strings = new ArrayList<String>();
    private int vectorSize = 0;

    public StringList(int vectorSize) {
        this.vectorSize = vectorSize;
    }

    public StringList(String[] strings) {
        this.vectorSize = strings.length;
        this.strings.addAll(Arrays.asList(strings));
    }

    public StringList(int vectorSize, String[] strings) {
        this.vectorSize = vectorSize;
        this.strings.addAll(Arrays.asList(strings));
    }

    public StringList(JSONObject json) {
        this.vectorSize = json.getInt("maxVectorSize");
        JSONArray jsonTypes = json.getJSONArray("types");
        for(int i = 0; i < jsonTypes.size(); i++) {
            String s = jsonTypes.getString(i);
            this.strings.add(s);
        }
    }

    public List<String> values() {
        return this.strings;
    }

    public int size() {
        return this.strings.size();
    }

    public String get(int i) {
        if (i >= this.strings.size()) {
            return null;
        }
        return this.strings.get(i);
    }

    public int ordinal(String w) {
        return this.strings.indexOf(w);
    }

    public StringList add(String w) {
        if(StringUtils.isBlank(w)) {
            return this;
        }
        if(this.strings.indexOf(w) >= 0) {
            return this;
        }
        this.strings.add(w);
        return this;
    }

    public int getVectorSize() {
        return this.vectorSize;
    }

    public Tensor1D word2vec(String w) {
        Tensor1D result = new Tensor1D(this.vectorSize);
        if (StringUtils.isBlank(w)) {
            return result;
        }
        return result.oneHot(this.ordinal(w));
    }

    public Tensor1D embedding(String w) {
        if (StringUtils.isBlank(w)) {
            return Tensor1D.Null;
        }
        return new Tensor1D(new Float[] { (float) this.ordinal(w) });
    }

    public JSONObject toJSON() {
        JSONArray jsonTypes = JSON.newJSONArray();
        for (String t : this.strings) {
            jsonTypes.append(t);
        }

        JSONObject json = JSON.newJSONObject();
        json.setInt("maxVectorSize", this.vectorSize);
        json.setJSONArray("types", jsonTypes);
        return json;
    }

    @Override
    public String toString() {
        return this.strings.toString();
    }
}
