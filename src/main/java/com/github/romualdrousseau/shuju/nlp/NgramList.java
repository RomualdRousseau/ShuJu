package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor1D;
import com.github.romualdrousseau.shuju.nlp.impl.NgramTokenizer;
import com.github.romualdrousseau.shuju.nlp.impl.ShingleTokenizer;
import com.github.romualdrousseau.shuju.util.StringUtility;

public class NgramList implements BaseList {
    public static final int SHINGLE = 0;

    private ArrayList<String> ngrams = new ArrayList<String>();
    private ArrayList<String> lexicon = new ArrayList<String>();
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

        JSONArray jsonNgrams = json.getJSONArray("ngrams");
        for (int i = 0; i < jsonNgrams.size(); i++) {
            String ngram = jsonNgrams.getString(i);
            if (!StringUtility.isEmpty(ngram)) {
                this.ngrams.add(ngram);
            }
        }

        JSONArray jsonLexicon = json.getJSONArray("lexicon");
        for (int i = 0; i < jsonLexicon.size(); i++) {
            String word = jsonLexicon.getString(i);
            if (!StringUtility.isEmpty(word)) {
                this.lexicon.add(word);
            }
        }
    }

    public NgramList(int n, int vectorSize, String[] ngrams, String[] lexicon) {
        this.n = n;
        this.vectorSize = vectorSize;

        if (ngrams != null) {
            this.ngrams.addAll(Arrays.asList(ngrams));
        }

        if (lexicon != null) {
            this.lexicon.addAll(Arrays.asList(lexicon));
        }

        if (this.n == NgramList.SHINGLE) {
            this.tokenizer = new ShingleTokenizer(this.ngrams, this.lexicon);
        } else {
            this.tokenizer = new NgramTokenizer(this.ngrams, this.n);
        }
    }

    public void setCustomTokenizer(ITokenizer tokenizer) {
        this.tokenizer = tokenizer;
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
        if(StringUtility.isEmpty(w)) {
            return this;
        }

        String[] tokens = this.tokenizer.tokenize(w);
        for (int i = 0; i < tokens.length; i++) {
            String s = tokens[i];
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

    public Tensor1D word2vec(String w) {
        Tensor1D result = new Tensor1D(this.vectorSize);
        if (StringUtility.isEmpty(w)) {
            return result;
        } else {
            return this.tokenizer.word2vec(w, result);
        }
    }

    public JSONObject toJSON() {
        JSONArray jsonNgrams = JSON.newJSONArray();
        for (String ngram : this.ngrams) {
            jsonNgrams.append(ngram);
        }

        JSONArray jsonLexicon = JSON.newJSONArray();
        for (String lexem : this.lexicon) {
            jsonLexicon.append(lexem);
        }

        JSONObject json = JSON.newJSONObject();
        json.setInt("n", this.n);
        json.setInt("maxVectorSize", this.vectorSize);
        json.setJSONArray("ngrams", jsonNgrams);
        json.setJSONArray("lexicon", jsonLexicon);
        return json;
    }

    @Override
    public String toString() {
        return this.ngrams.toString();
    }
}
