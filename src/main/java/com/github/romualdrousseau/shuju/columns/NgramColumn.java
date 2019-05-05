package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.nlp.NgramList;

public class NgramColumn implements IColumn<String> {
    private NgramList ngrams;

    public NgramColumn(NgramList ngrams) {
        this.ngrams = ngrams;
    }

    public Vector valueOf(String w) {
        return this.ngrams.word2vec(w);
    }
}
