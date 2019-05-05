package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.nlp.ShingleList;

public class ShingleColumn implements IColumn<String> {
    private ShingleList shingles;

    public ShingleColumn(ShingleList shingles) {
        this.shingles = shingles;
    }

    public Vector valueOf(String w) {
        return this.shingles.word2vec(w);
    }
}
