package com.github.romualdrousseau.shuju.nlp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.util.FuzzyString;
import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;

public class NgramList implements BaseList {
    private ArrayList<String> ngrams = new ArrayList<String>();
    private ArrayList<String> lexicon = new ArrayList<String>();
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

    public NgramList(int n, int vectorSize, String[] ngrams, String[] lexicon) {
        this.n = n;
        this.vectorSize = vectorSize;
        this.ngrams.addAll(Arrays.asList(ngrams));
        this.lexicon.addAll(Arrays.asList(lexicon));
    }

    public NgramList(JSONObject json) {
        this.n = json.getInt("n");
        this.vectorSize = json.getInt("maxVectorSize");
        JSONArray jsonNgrams = json.getJSONArray("ngrams");
        for (int i = 0; i < jsonNgrams.size(); i++) {
            String p = jsonNgrams.getString(i);
            this.ngrams.add(p);
        }
        JSONArray jsonLexicon = json.getJSONArray("lexicon");
        for (int i = 0; i < jsonLexicon.size(); i++) {
            String p = jsonLexicon.getString(i);
            this.lexicon.add(p);
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
        if (this.n == 0) {
            String[] tokens = this.tokenize(w);
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
        } else {
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
        }
        return this;
    }

    public int getVectorSize() {
        return this.vectorSize;
    }

    public Vector word2vec(String w) {
        Vector result = new Vector(this.vectorSize);

        if (w == null) {
            return result;
        }

        if (this.n == 0) {
            String[] tokens = this.tokenize(w);
            for (int i = 0; i < tokens.length; i++) {
                String p = tokens[i];
                for (int j = 0; j < this.ngrams.size(); j++) {
                    float f = FuzzyString.JaroWinkler(p, this.ngrams.get(j));
                    if(f > 0.9f) {
                        result.set(j, f);
                    }
                }
            }
        } else {
            for (int i = 0; i < w.length() - this.n + 1; i++) {
                String p = w.substring(i, i + this.n).toLowerCase();
                for (int j = 0; j < this.ngrams.size(); j++) {
                    if(p.equals(this.ngrams.get(j))) {
                        result.set(j, 1.0f);
                    }
                }
            }
        }

        return result;
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

    private String[] tokenize(String s) {
        if (this.lexicon.size() > 0) {
            String slc = s.toLowerCase();
            for (String lexem : this.lexicon) {
                if (slc.contains(lexem)) {
                    s = s.replaceAll("(?i)" + lexem, " " + lexem + " ");
                }
            }
        }

        s = s.replaceAll("[\\s_]+", " ").trim();

        ArrayList<String> result = new ArrayList<String>();
        for (String w : s.split(" ")) {
            for (String ww : w.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
                result.add(ww.toLowerCase());
            }
        }

        return result.toArray(new String[result.size()]);
    }
}
