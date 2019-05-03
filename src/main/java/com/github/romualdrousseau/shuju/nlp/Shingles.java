package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.util.FuzzyString;
import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class Shingles {
    private ArrayList<String> shingles = new ArrayList<String>();
    private int maxVectorSize = 0;

    public Shingles(int maxVectorSize) {
        this.maxVectorSize = maxVectorSize;
    }

    public Shingles(JSONObject json) {
        this.maxVectorSize = json.getInt("maxVectorSize");
        JSONArray jsonShingles = json.getJSONArray("shingles");
        for (int i = 0; i < jsonShingles.size(); i++) {
            String p = jsonShingles.getString(i);
            this.shingles.add(p);
        }
    }

    public List<String> shingles() {
        return this.shingles;
    }

    public int size() {
        return this.shingles.size();
    }

    public int ordinal(String shingle) {
        return this.shingles.indexOf(shingle);
    }

    public void registerWords(String words) {
        String[] w = words.split("[ _]");
        for (int i = 0; i < w.length; i++) {
            String s = w[i].trim().toLowerCase();
            int index = this.shingles.indexOf(s);
            if (index >= 0) {
                continue;
            }

            this.shingles.add(s);
            if (this.shingles.size() >= this.maxVectorSize) {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    public Vector word2vec(String w) {
        Vector result = new Vector(this.maxVectorSize);

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
        json.setInt("maxVectorSize", this.maxVectorSize);
        json.setJSONArray("shingles", jsonShingles);
        return json;
    }

    protected float similarity(String s1, String s2) {
        return FuzzyString.JaroWinkler(s1, s2);
    }
}
