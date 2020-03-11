package com.github.romualdrousseau.shuju.ml.nn.layer.builder;

import com.github.romualdrousseau.shuju.ml.nn.NormalizerFunc;
import com.github.romualdrousseau.shuju.ml.nn.LayerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.layer.Normalizer;
import com.github.romualdrousseau.shuju.ml.nn.normalizer.BatchNormalizer;

public class NormalizerBuilder extends LayerBuilder<Normalizer> {

    public NormalizerBuilder() {
        super();
        this.normalizer = new BatchNormalizer();
    }

    public Normalizer build() {
        return new Normalizer(this.normalizer);
    }

    public NormalizerBuilder setNormalizer(NormalizerFunc normalizer) {
        this.normalizer = normalizer;
        return this;
    }

    private NormalizerFunc normalizer;
}
