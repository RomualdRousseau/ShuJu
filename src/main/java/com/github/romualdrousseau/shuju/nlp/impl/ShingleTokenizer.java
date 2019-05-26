package com.github.romualdrousseau.shuju.nlp.impl;

import java.util.ArrayList;

import com.github.romualdrousseau.shuju.math.Vector;
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

    public String[] tokenize(String s) {
        s = StringUtility.normalizeWhiteSpaces(s);

        // Split using a lexicon of known words if any
        if (this.lexicon.size() > 0) {
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
        for (String w : s.split(" ")) {
            for (String ww : w.split("(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])")) {
                result.add(ww.toLowerCase());
            }
        }

        return result.toArray(new String[result.size()]);
    }

    public Vector word2vec(String s, Vector outVector) {
        String[] tokens = this.tokenize(s);
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            for (int j = 0; j < this.shingles.size(); j++) {
                float f = this.similarity(token, this.shingles.get(j));
                // TODO: test this one
                if (f > 0.8f) {
                    outVector.set(j, f);
                }
            }
        }
        return outVector;
    }

    private float similarity(String s1, String s2) {
        return FuzzyString.JaroWinkler(s1, s2);
    }
}
