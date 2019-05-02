package com.github.romualdrousseau.shuju.nlp;

import java.util.HashMap;

import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.util.FuzzyString;
import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;

public class Shingles {
    private HashMap<String, Integer> shingles = new HashMap<String, Integer>();
    private int maxVectorSize = 0;
    private int shinglesCount = 0;

    public Shingles(int maxVectorSize) {
        this.maxVectorSize = maxVectorSize;
    }

    public Shingles(JSONObject json) {
        this.maxVectorSize = json.getInt("maxVectorSize");
        JSONArray jsonShingles = json.getJSONArray("shingles");
        for (int i = 0; i < jsonShingles.size(); i++) {
            String p = jsonShingles.getString(i);
            this.shingles.put(p, i);
        }
        this.shinglesCount = jsonShingles.size();
    }

    public void registerWords(String words) {
        String[] w = words.split("[ _]");
        for (int i = 0; i < w.length; i++) {
            String s = w[i].trim().toLowerCase();
            Integer index = this.shingles.get(s);
            if (index != null) {
                continue;
            }

            index = this.shinglesCount;
            this.shingles.put(s, index);
            this.shinglesCount++;
            if (this.shinglesCount >= this.maxVectorSize) {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    public Vector word2vec(String w) {
        Vector result = new Vector(this.maxVectorSize);

        for (String key: shingles.keySet()) {
            Integer index = this.shingles.get(key);
            if (index != null && index < this.maxVectorSize) {
                result.set(index, FuzzyString.similarity(key, w));
            }
        }

        return result;
    }

    public JSONObject toJSON() {
        JSONArray jsonShingles = JSON.getFactory().newJSONArray();
        for (String shingle : this.shingles.keySet()) {
            int index = this.shingles.get(shingle);
            jsonShingles.setString(index, shingle);
        }

        JSONObject json = JSON.getFactory().newJSONObject();
        json.setInt("maxVectorSize", this.maxVectorSize);
        json.setJSONArray("shingles", jsonShingles);
        return json;
    }
}
