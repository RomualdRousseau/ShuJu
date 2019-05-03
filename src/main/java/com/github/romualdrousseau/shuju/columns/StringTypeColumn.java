package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.nlp.StringTypes;

public class StringTypeColumn implements IColumn<String> {
    private StringTypes stringTypes;

    public StringTypeColumn(StringTypes stringTypes) {
        this.stringTypes = stringTypes;
    }

    public Vector valueOf(String w) {
        return this.stringTypes.word2vec(w);
    }
}
