package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;

public class Ngrams {
    private ArrayList<String> ngrams = new ArrayList<String>();
    private int n;
    private int maxVectorSize = 0;

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
            this.ngrams.add(p);
        }
    }

    public List<String> ngrams() {
        return this.ngrams;
    }

    public int ordinal(String ngram) {
        return this.ngrams.indexOf(ngram);
    }

    public void registerWord(String word) {
        for (int i = 0; i < word.length() - this.n + 1; i++) {
            String s = word.substring(i, i + this.n).toLowerCase();
            int index = this.ngrams.indexOf(s);
            if (index >= 0) {
                continue;
            }

            this.ngrams.add(s);
            if (this.ngrams.size() >= this.maxVectorSize) {
                throw new IndexOutOfBoundsException();
            }
        }
    }

    public Vector word2vec(String w) {
        Vector result = new Vector(this.maxVectorSize);

        if(w == null) {
            return result;
        }

        for (int i = 0; i < w.length() - this.n + 1; i++) {
            String p = w.substring(i, i + this.n).toLowerCase();
            int index = this.ngrams.indexOf(p);
            if (index >= 0 && index < this.maxVectorSize) {
                result.set(index, 1.0f);
            }
        }

        return result;
    }

    public JSONObject toJSON() {
        JSONArray jsonNgrams = JSON.newJSONArray();
        for (String ngram : this.ngrams) {
            jsonNgrams.append(ngram);
        }

        JSONObject json = JSON.newJSONObject();
        json.setInt("n", this.n);
        json.setInt("maxVectorSize", this.maxVectorSize);
        json.setJSONArray("ngrams", jsonNgrams);
        return json;
    }
}
