package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor1D;
import com.github.romualdrousseau.shuju.nlp.NgramList;

public class NgramColumn implements IColumn<String> {
    private NgramList ngrams;

    public NgramColumn(NgramList ngrams) {
        this.ngrams = ngrams;
    }

    public Tensor1D valueOf(String w) {
        return this.ngrams.word2vec(w);
    }
}
