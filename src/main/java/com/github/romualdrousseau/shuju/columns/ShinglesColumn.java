package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.nlp.Shingles;

public class ShinglesColumn implements IColumn<String> {
    private Shingles shingles;

    public ShinglesColumn(Shingles shingles) {
        this.shingles = shingles;
    }

    public Vector valueOf(String w) {
        return this.shingles.word2vec(w);
    }
}
