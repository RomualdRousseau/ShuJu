package com.github.romualdrousseau.shuju.nlp.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor1D;
import com.github.romualdrousseau.shuju.nlp.ITokenizer;
import com.github.romualdrousseau.shuju.util.FuzzyString;
import com.github.romualdrousseau.shuju.util.StringUtility;

public class ShingleTokenizer implements ITokenizer {
    private ArrayList<String> shingles = new ArrayList<String>();
    private ArrayList<String> lexicon = new ArrayList<String>();

    public ShingleTokenizer(String[] shingles, String[] lexicon) {
        this(Arrays.asList(shingles), Arrays.asList(lexicon));
    }

    public ShingleTokenizer(List<String> shingles, List<String> lexicon) {
        this.shingles.addAll(shingles);
        this.lexicon.addAll(lexicon);
    }

    public ShingleTokenizer(JSONObject json) {
        JSONArray jsonShingles = json.getJSONArray("shingles");
        for (int i = 0; i < jsonShingles.size(); i++) {
            String shingle = jsonShingles.getString(i);
            if (!StringUtility.isEmpty(shingle)) {
                this.shingles.add(shingle);
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

    @Override
    public List<String> values() {
        return this.shingles;
    }

    @Override
    public void add(String s) {
        if ((s.length() > 1 || Character.isDigit(s.charAt(0))) && this.shingles.indexOf(s) < 0) {
            this.shingles.add(s);
        }
    }

    @Override
    public String[] tokenize(String w) {
        String s = StringUtility.normalizeWhiteSpaces(w);

        // Split using a lexicon of known words if any

        if (this.lexicon != null && this.lexicon.size() > 0) {
            String slc = s.toLowerCase();
            for (String lexem : this.lexicon) {
                for(String variant : this.getVariants(lexem)) {
                    if (slc.contains(variant)) {
                        s = s.replaceAll("(?i)" + variant, " " + variant + " ");
                        break;
                    }
                }
            }
        }

        // Split by space and underscore

        s = s.replaceAll("[\\s_]+", " ").trim();

        // Split Camel notation words

        ArrayList<String> result = new ArrayList<String>();
        for (String ss : s.split(" ")) {
            for (String sss : ss.split("(?<!(^|[A-Z/]))(?=[A-Z/])|(?<!^)(?=[A-Z/][a-z/])")) {
                result.add(sss.toLowerCase());
            }
        }

        return result.toArray(new String[result.size()]);
    }

    @Override
    public Tensor1D word2vec(String s, Tensor1D outVector) {
        String[] tokens = this.tokenize(s);
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            for (int j = 0; j < this.shingles.size(); j++) {
                if (this.similarity(token, this.shingles.get(j)) > 0.95f) {
                    outVector.set(j, 1.0f);
                }
            }
        }
        return outVector;
    }

    @Override
    public JSONObject toJSON() {
        JSONArray jsonShingles = JSON.newJSONArray();
        for (String shingle : this.shingles) {
            jsonShingles.append(shingle);
        }

        JSONArray jsonLexicon = JSON.newJSONArray();
        for (String word : this.lexicon) {
            jsonLexicon.append(word);
        }

        JSONObject json = JSON.newJSONObject();
        json.setJSONArray("shingles", jsonShingles);
        json.setJSONArray("lexicon", jsonLexicon);
        return json;
    }

    private List<String> getVariants(String lexem) {
        ArrayList<String> result = new ArrayList<String>();
        for (String other : this.lexicon) {
            if(other.contains(lexem)) {
                result.add(other);
            }
        }
        result.sort((w1, w2) -> w2.length() - w1.length());
        return result;
    }

    private float similarity(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return 0.0f;
        } else {
            return FuzzyString.JaroWinkler(s1, s2);
        }
    }
}
