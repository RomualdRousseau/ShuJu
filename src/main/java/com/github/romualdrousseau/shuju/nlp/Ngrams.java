package com.github.romualdrousseau.shuju.nlp;

import java.util.HashMap;

import com.github.romualdrousseau.shuju.json.JSONFactory;
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

    public void registerWord(String w) {
        for (int i = 0; i < w.length() - this.n + 1; i++) {
            String s = w.substring(i, i + this.n).toLowerCase();
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

    public float[] word2vec(String w) {
        float[] result = new float[this.maxVectorSize];

        for (int i = 0; i < w.length() - this.n + 1; i++) {
            String p = w.substring(i, i + this.n).toLowerCase();
            Integer index = this.ngrams.get(p);
            if (index != null && index < this.maxVectorSize) {
                result[index] = 1;
            }
        }

        return result;
    }

    public void fromJSON(JSONArray jsonNgrams) {
        this.ngrams.clear();
        for (int i = 0; i < jsonNgrams.size(); i++) {
            String p = jsonNgrams.getString(i);
            this.ngrams.put(p, i);
        }
        this.ngramsCount = jsonNgrams.size();
    }

    public JSONArray toJSON(JSONFactory jsonFactory) {
        JSONArray jsonNgrams = jsonFactory.newJSONArray();
        for (String ngram : this.ngrams.keySet()) {
            int index = this.ngrams.get(ngram);
            jsonNgrams.setString(index, ngram);
        }
        return jsonNgrams;
    }
}
