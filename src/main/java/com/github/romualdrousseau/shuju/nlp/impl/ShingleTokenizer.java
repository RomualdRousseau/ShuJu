package com.github.romualdrousseau.shuju.nlp.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor1D;
import com.github.romualdrousseau.shuju.nlp.ITokenizer;
import com.github.romualdrousseau.shuju.util.FuzzyString;
import com.github.romualdrousseau.shuju.util.StringUtility;

public class ShingleTokenizer implements ITokenizer {
    private ArrayList<String> shingles = new ArrayList<String>();
    private HashMap<String, Integer> shinglesIndex = new HashMap<String, Integer>();
    private ArrayList<String> lexicon = new ArrayList<String>();
    private HashMap<String, List<String>> variants = new HashMap<String, List<String>>();

    private static Pattern camelPattern = Pattern.compile("(?<!(^|[A-Z/]))(?=[A-Z/])|(?<!^)(?=[A-Z/][a-z/])");

    public ShingleTokenizer(String[] shingles, String[] lexicon) {
        this(Arrays.asList(shingles), Arrays.asList(lexicon));
    }

    public ShingleTokenizer(List<String> shingles, List<String> lexicon) {
        this.shingles.addAll(shingles);
        this.lexicon.addAll(lexicon);
        this.rebuildShinglesIndex();
        this.rebuildVariants();
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
        this.rebuildShinglesIndex();
        this.rebuildVariants();
    }

    @Override
    public List<String> values() {
        return this.shingles;
    }

    @Override
    public void add(String s) {
        if ((s.length() > 1 || Character.isDigit(s.charAt(0))) && !this.shinglesIndex.containsKey(s)) {
            this.shingles.add(s);
        }
        this.rebuildShinglesIndex();
    }

    @Override
    public List<String> tokenize(String w) {
        String s = StringUtility.normalizeWhiteSpaces(w);

        // Split using a lexicon of known words if any

        if (this.lexicon.size() > 0) {
            String slc = s.toLowerCase();
            for (String lexem : this.lexicon) {
                for (String variant : this.variants.get(lexem)) {
                    if (slc.contains(variant)) {
                        s = s.replaceAll("(?i)" + variant, " " + variant + " ");
                        break;
                    }
                }
            }
        }

        // Clean by space and underscore

        s = s.replaceAll("[\\s_]+", " ").trim();

        // Split by space and then by Camel notation words

        ArrayList<String> result = new ArrayList<String>();
        for (String ss : s.split(" ")) {
            for (String sss : camelPattern.split(ss)) {
                result.add(sss.toLowerCase());
            }
        }

        return result;
    }

    @Override
    public Tensor1D word2vec(final String s, final Tensor1D outVector) {
        this.tokenize(s).forEach(token -> {
            Optional.ofNullable(this.shinglesIndex.get(token))
                .map(j -> {
                    return outVector.set(j, 1.0f);
                })
                .orElseGet(() -> {
                    for (int j = 0; j < this.shingles.size(); j++) {
                        if (this.similarity(token, this.shingles.get(j)) > 0.95f) {
                            outVector.set(j, 1.0f);
                        }
                    }
                    return outVector;
                });
        });
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

    private float similarity(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return 0.0f;
        } else {
            return FuzzyString.JaroWinkler(s1, s2);
        }
    }

    private void rebuildShinglesIndex() {
        for (int i = 0; i < shingles.size(); i++) {
            this.shinglesIndex.put(this.shingles.get(i), i);
        }
    }

    private void rebuildVariants() {
        this.variants.clear();
        for (String lexem : this.lexicon) {
            this.variants.put(lexem, this.getVariants(lexem));
        }
    }

    private List<String> getVariants(String lexem) {
        ArrayList<String> result = new ArrayList<String>();
        for (String other : this.lexicon) {
            if (other.contains(lexem)) {
                result.add(other);
            }
        }
        result.sort((w1, w2) -> w2.length() - w1.length());
        return result;
    }
}
