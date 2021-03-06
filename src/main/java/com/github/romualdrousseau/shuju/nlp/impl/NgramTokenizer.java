package com.github.romualdrousseau.shuju.nlp.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor1D;
import com.github.romualdrousseau.shuju.nlp.ITokenizer;
import com.github.romualdrousseau.shuju.util.StringUtility;

public class NgramTokenizer implements ITokenizer {
    private ArrayList<String> ngrams = new ArrayList<String>();
    private int n;

    public NgramTokenizer(String[] ngrams, int n) {
        this(Arrays.asList(ngrams), n);
    }

    public NgramTokenizer(List<String> ngrams, int n) {
        this.ngrams.addAll(ngrams);
        this.n = n;
    }

    public NgramTokenizer(JSONObject json) {
        JSONArray jsonNgrams = json.getJSONArray("ngrams");
        for (int i = 0; i < jsonNgrams.size(); i++) {
            String ngram = jsonNgrams.getString(i);
            if (!StringUtility.isEmpty(ngram)) {
                this.ngrams.add(ngram);
            }
        }
        this.n = json.getInt("n");
    }

    @Override
    public List<String> values() {
        return this.ngrams;
    }

    @Override
    public void add(String s) {
        if(this.ngrams.indexOf(s) < 0) {
            this.ngrams.add(s);
        }
    }

    @Override
    public String[] tokenize(String s) {
        s = StringUtility.normalizeWhiteSpaces(s);

        // Join by space and underscore
        s = s.replaceAll("[\\s_]+", "").trim();

        // Fill up with ? to have at least one token
        while (s.length() < n) {
            s += "?";
        }

        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < s.length() - this.n + 1; i++) {
            String w = s.substring(i, i + this.n);
            result.add(w.toLowerCase());
        }

        return result.toArray(new String[result.size()]);
    }

    @Override
    public Tensor1D word2vec(String s, Tensor1D outVector) {
        String[] tokens = this.tokenize(s);
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            for (int j = 0; j < this.ngrams.size(); j++) {
                if (token.equals(this.ngrams.get(j))) {
                    outVector.set(j, 1.0f);
                }
            }
        }
        return outVector;
    }

    @Override
    public JSONObject toJSON() {
        JSONArray jsonNgrams = JSON.newJSONArray();
        for (String ngram : this.ngrams) {
            jsonNgrams.append(ngram);
        }

        JSONObject json = JSON.newJSONObject();
        json.setJSONArray("ngrams", jsonNgrams);
        json.setInt("n", this.n);
        return json;
    }
}
