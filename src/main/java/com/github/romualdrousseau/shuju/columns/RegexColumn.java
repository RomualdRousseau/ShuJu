package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor1D;
import com.github.romualdrousseau.shuju.nlp.RegexList;

public class RegexColumn implements IColumn<String> {
    private RegexList entityTypes;

    public RegexColumn(RegexList entityTypes) {
        this.entityTypes = entityTypes;
    }

    public Tensor1D valueOf(String w) {
        return this.entityTypes.word2vec(w);
    }
}
