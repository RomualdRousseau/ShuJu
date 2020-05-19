package com.github.romualdrousseau.shuju.nlp.impl;

import java.util.ArrayList;

import com.github.romualdrousseau.shuju.math.Tensor1D;
import com.github.romualdrousseau.shuju.nlp.ITokenizer;
import com.github.romualdrousseau.shuju.util.FuzzyString;
import com.github.romualdrousseau.shuju.util.StringUtility;

public class ShingleTokenizer implements ITokenizer {
    private ArrayList<String> shingles;
    private ArrayList<String> lexicon;

    public ShingleTokenizer(ArrayList<String> shingles, ArrayList<String> lexicon) {
        this.shingles = shingles;
        this.lexicon = lexicon;
    }

    @Override
    public void add(String s) {
        this.shingles.add(s);
    }

    @Override
    public String[] tokenize(String w) {
        String s = StringUtility.normalizeWhiteSpaces(w);

        // Split using a lexicon of known words if any
        if (this.lexicon != null && this.lexicon.size() > 0) {
            String slc = s.toLowerCase();
            for (String lexem : this.lexicon) {
                if (slc.contains(lexem)) {
                    s = s.replaceAll("(?i)" + lexem, " " + lexem + " ");
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
                if (this.similarity(token, this.shingles.get(j)) > 0.99f) {
                    outVector.set(j, 1.0f);
                }
            }
        }
        return outVector;
    }

    private float similarity(String s1, String s2) {
        return FuzzyString.JaroWinkler(s1, s2);
    }
}
