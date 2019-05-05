package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.nlp.StringList;

public class StringColumn implements IColumn<String> {
    private StringList stringTypes;

    public StringColumn(StringList stringTypes) {
        this.stringTypes = stringTypes;
    }

    public Vector valueOf(String w) {
        return this.stringTypes.word2vec(w);
    }
}
