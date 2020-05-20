package com.github.romualdrousseau.shuju.nlp;

import java.util.List;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor1D;
import com.github.romualdrousseau.shuju.nlp.impl.NgramTokenizer;
import com.github.romualdrousseau.shuju.nlp.impl.ShingleTokenizer;
import com.github.romualdrousseau.shuju.util.StringUtility;

public class NgramList implements BaseList {
    public static final int SHINGLE = 0;

    private int n;
    private int vectorSize = 0;
    private ITokenizer tokenizer;

    public NgramList(int n, int vectorSize) {
        this(n, vectorSize, null, null);
    }

    public NgramList(int n, int vectorSize, String[] ngrams) {
        this(n, vectorSize, ngrams, null);
    }

    public NgramList(JSONObject json) {
        this(json.getInt("n"), json.getInt("maxVectorSize"), null, null);
        if (this.n == NgramList.SHINGLE) {
            this.tokenizer = new ShingleTokenizer(json.getJSONObject("tokenizer"));
        } else {
            this.tokenizer = new NgramTokenizer(json.getJSONObject("tokenizer"));
        }
    }

    public NgramList(int n, int vectorSize, String[] ngrams, String[] lexicon) {
        this.n = n;
        this.vectorSize = vectorSize;
        if (ngrams == null) {
            ngrams = new String[] {};
        }
        if (lexicon == null) {
            lexicon = new String[] {};
        }
        if (this.n == NgramList.SHINGLE) {
            this.tokenizer = new ShingleTokenizer(ngrams, lexicon);
        } else {
            this.tokenizer = new NgramTokenizer(ngrams, this.n);
        }
    }

    public void setCustomTokenizer(ITokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public List<String> values() {
        return this.tokenizer.values();
    }

    public int size() {
        return this.values().size();
    }

    public String get(int i) {
        if (i >= this.size()) {
            return null;
        }
        return this.values().get(i);
    }

    public int ordinal(String w) {
        return this.values().indexOf(w);
    }

    public NgramList add(String w) {
        if(StringUtility.isEmpty(w)) {
            return this;
        }

        String[] tokens = this.tokenizer.tokenize(w);
        for (int i = 0; i < tokens.length; i++) {
            this.tokenizer.add(tokens[i]);
            if (this.size() >= this.vectorSize) {
                throw new IndexOutOfBoundsException();
            }
        }

        return this;
    }

    public int getVectorSize() {
        return this.vectorSize;
    }

    public Tensor1D word2vec(String w) {
        Tensor1D result = new Tensor1D(this.vectorSize);
        if (StringUtility.isEmpty(w)) {
            return result;
        } else {
            return this.tokenizer.word2vec(w, result);
        }
    }

    public JSONObject toJSON() {
        JSONObject json = JSON.newJSONObject();
        json.setInt("n", this.n);
        json.setInt("maxVectorSize", this.vectorSize);
        json.setJSONObject("tokenizer", this.tokenizer.toJSON());
        return json;
    }

    @Override
    public String toString() {
        return this.values().toString();
    }
}
