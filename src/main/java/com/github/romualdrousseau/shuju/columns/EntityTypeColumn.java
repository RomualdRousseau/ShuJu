package com.github.romualdrousseau.shuju.columns;

import com.github.romualdrousseau.shuju.IColumn;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.nlp.EntityTypes;

public class EntityTypeColumn implements IColumn<String> {
    private EntityTypes entityTypes;

    public EntityTypeColumn(EntityTypes entityTypes) {
        this.entityTypes = entityTypes;
    }

    public Vector valueOf(String w) {
        return this.entityTypes.word2vec(w);
    }
}
