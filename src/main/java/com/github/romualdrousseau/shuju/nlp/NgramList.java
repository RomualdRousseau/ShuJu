package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;

public class NgramList implements BaseList {
    private ArrayList<String> ngrams = new ArrayList<String>();
    private int n;
    private int vectorSize = 0;

    public NgramList(int n, int vectorSize) {
        this.n = n;
        this.vectorSize = vectorSize;
    }

    public NgramList(int n, int vectorSize, String[] ngrams) {
        this.n = n;
        this.vectorSize = vectorSize;
        this.ngrams.addAll(Arrays.asList(ngrams));
    }

    public NgramList(JSONObject json) {
        this.n = json.getInt("n");
        this.vectorSize = json.getInt("maxVectorSize");
        JSONArray jsonNgrams = json.getJSONArray("ngrams");
        for (int i = 0; i < jsonNgrams.size(); i++) {
            String p = jsonNgrams.getString(i);
            this.ngrams.add(p);
        }
    }

    public List<String> values() {
        return this.ngrams;
    }

    public int size() {
        return this.ngrams.size();
    }

    public String get(int i) {
        return this.ngrams.get(i);
    }

    public int ordinal(String w) {
        return this.ngrams.indexOf(w);
    }

    public NgramList add(String w) {
        for (int i = 0; i < w.length() - this.n + 1; i++) {
            String s = w.substring(i, i + this.n).toLowerCase();
            int index = this.ngrams.indexOf(s);
            if (index >= 0) {
                continue;
            }

            this.ngrams.add(s);
            if (this.ngrams.size() >= this.vectorSize) {
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

        for (int i = 0; i < w.length() - this.n + 1; i++) {
            String p = w.substring(i, i + this.n).toLowerCase();
            int index = this.ngrams.indexOf(p);
            if (index >= 0 && index < this.vectorSize) {
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
        json.setInt("maxVectorSize", this.vectorSize);
        json.setJSONArray("ngrams", jsonNgrams);
        return json;
    }
}
