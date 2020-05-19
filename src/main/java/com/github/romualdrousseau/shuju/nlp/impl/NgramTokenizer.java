package com.github.romualdrousseau.shuju.nlp.impl;

import java.util.ArrayList;

import com.github.romualdrousseau.shuju.math.Tensor1D;
import com.github.romualdrousseau.shuju.nlp.ITokenizer;
import com.github.romualdrousseau.shuju.util.StringUtility;

public class NgramTokenizer implements ITokenizer {
    private ArrayList<String> ngrams;
    private int n;

    public NgramTokenizer(ArrayList<String> ngrams, int n) {
        this.ngrams = ngrams;
        this.n = n;
    }

    @Override
    public void add(String s) {
        this.ngrams.add(s);
    }

    @Override
    public String[] tokenize(String s) {
        s = StringUtility.normalizeWhiteSpaces(s);

        // Join by space and underscore
        s = s.replaceAll("[\\s_]+", "").trim();

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
}
