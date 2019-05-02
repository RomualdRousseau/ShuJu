package com.github.romualdrousseau.shuju.nlp;

import java.util.HashMap;

import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;

public class Ngrams {
    private HashMap<String, Integer> ngrams = new HashMap<String, Integer>();
    private int n;
    private int maxVectorSize = 0;
    private int ngramsCount = 0;

    public Ngrams(int n, int maxVectorSize) {
        this.n = n;
        this.maxVectorSize = maxVectorSize;
    }

    public Ngrams(JSONObject json) {
        this.n = json.getInt("n");
        this.maxVectorSize = json.getInt("maxVectorSize");
        JSONArray jsonNgrams = json.getJSONArray("ngrams");
        for (int i = 0; i < jsonNgrams.size(); i++) {
            String p = jsonNgrams.getString(i);
            this.ngrams.put(p, i);
        }
        this.ngramsCount = jsonNgrams.size();
    }

    public void registerWord(String word) {
        for (int i = 0; i < word.length() - this.n + 1; i++) {
            String s = word.substring(i, i + this.n).toLowerCase();
            Integer index = this.ngrams.get(s);
            if (index != null) {
                continue;
            }

            index = this.ngramsCount;
            this.ngrams.put(s, index);
            this.ngramsCount++;
            if (this.ngramsCount >= this.maxVectorSize) {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    public Vector word2vec(String w) {
        Vector result = new Vector(this.maxVectorSize);

        for (int i = 0; i < w.length() - this.n + 1; i++) {
            String p = w.substring(i, i + this.n).toLowerCase();
            Integer index = this.ngrams.get(p);
            if (index != null && index < this.maxVectorSize) {
                result.set(index, 1.0f);
            }
        }

        return result;
    }

    public JSONObject toJSON() {
        JSONArray jsonNgrams = JSON.getFactory().newJSONArray();
        for (String ngram : this.ngrams.keySet()) {
            int index = this.ngrams.get(ngram);
            jsonNgrams.setString(index, ngram);
        }

        JSONObject json = JSON.getFactory().newJSONObject();
        json.setInt("n", this.n);
        json.setInt("maxVectorSize", this.maxVectorSize);
        json.setJSONArray("ngrams", jsonNgrams);
        return json;
    }
}
