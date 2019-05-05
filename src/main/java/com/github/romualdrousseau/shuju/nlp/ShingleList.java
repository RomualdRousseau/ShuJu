package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.util.FuzzyString;
import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class ShingleList implements BaseList {
    private ArrayList<String> shingles = new ArrayList<String>();
    private int vectorSize = 0;

    public ShingleList(int vectorSize) {
        this.vectorSize = vectorSize;
    }

    public ShingleList(int vectorSize, String[] shingles) {
        this.vectorSize = vectorSize;
        this.shingles.addAll(Arrays.asList(shingles));
    }

    public ShingleList(JSONObject json) {
        this.vectorSize = json.getInt("maxVectorSize");
        JSONArray jsonShingles = json.getJSONArray("shingles");
        for (int i = 0; i < jsonShingles.size(); i++) {
            String p = jsonShingles.getString(i);
            this.shingles.add(p);
        }
    }

    public List<String> values() {
        return this.shingles;
    }

    public int size() {
        return this.shingles.size();
    }

    public String get(int i) {
        return this.shingles.get(i);
    }

    public int ordinal(String w) {
        return this.shingles.indexOf(w);
    }

    public ShingleList add(String s) {
        String[] tokens = this.tokenizes(s);
        for(String token: tokens) {
            int index = this.shingles.indexOf(token);
            if (index >= 0) {
                continue;
            }

            this.shingles.add(token);
            if (this.shingles.size() >= this.vectorSize) {
                throw new IndexOutOfBoundsException();
            }
        }
        return this;
    }

    public int getVectorSize() {
        return this.vectorSize;
    }

    public Vector word2vec(String w) {
        Vector result = new Vector(this.vectorSize);

        if(w == null) {
            return result;
        }

        for(int i = 0; i < this.shingles.size(); i++) {
            result.set(i, this.similarity(this.shingles.get(i), w.toLowerCase()));
        }

        return result;
    }

    public JSONObject toJSON() {
        JSONArray jsonShingles = JSON.newJSONArray();
        for (String shingle : this.shingles) {
            jsonShingles.append(shingle);
        }

        JSONObject json = JSON.newJSONObject();
        json.setInt("maxVectorSize", this.vectorSize);
        json.setJSONArray("shingles", jsonShingles);
        return json;
    }

    protected String[] tokenizes(String s) {
        String[] tokens = s.split("[ _]"); // TODO: Add CAMEL processing
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim().toLowerCase();
        }
        return tokens;
    }

    protected float similarity(String s1, String s2) {
        return FuzzyString.JaroWinkler(s1, s2);
    }
}
