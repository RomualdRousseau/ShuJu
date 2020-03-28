package com.github.romualdrousseau.shuju.ml.nn.layer.builder;

import com.github.romualdrousseau.shuju.ml.nn.LayerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.layer.BatchNormalizer;

public class BatchNormalizerBuilder extends LayerBuilder<BatchNormalizer> {

    public BatchNormalizerBuilder() {
        super();
        this.inputChannels = 0;
    }

    public BatchNormalizer build() {
        return new BatchNormalizer(this.inputUnits, this.inputChannels);
    }

    public BatchNormalizerBuilder setInputUnits(int inputUnits) {
        this.inputUnits = inputUnits;
        return this;
    }

    public BatchNormalizerBuilder setInputChannels(int inputChannels) {
        this.inputChannels = inputChannels;
        return this;
    }
}
