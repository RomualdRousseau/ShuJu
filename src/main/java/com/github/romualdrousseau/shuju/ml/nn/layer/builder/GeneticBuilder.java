package com.github.romualdrousseau.shuju.ml.nn.layer.builder;

import com.github.romualdrousseau.shuju.ml.nn.InitializerFunc;
import com.github.romualdrousseau.shuju.ml.nn.LayerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.RegularizerFunc;
import com.github.romualdrousseau.shuju.ml.nn.layer.Genetic;
import com.github.romualdrousseau.shuju.ml.nn.initializer.GlorotUniformInitializer;

public class GeneticBuilder extends LayerBuilder<Genetic> {

    public GeneticBuilder() {
        super();
        this.units = 0;
        this.initializer = new GlorotUniformInitializer();
        this.regularizer = null;
        this.mutationRate = 1.0f;
    }

    public Genetic build() {
        return new Genetic(this.inputUnits, this.units, this.bias, this.initializer, this.regularizer, this.mutationRate);
    }

    public GeneticBuilder setBias(float bias) {
        this.bias = bias;
        return this;
    }

    public GeneticBuilder setInputUnits(int inputUnits) {
        this.inputUnits = inputUnits;
        return this;
    }

    public GeneticBuilder setUnits(int units) {
        this.units = units;
        return this;
    }

    public GeneticBuilder setInitializer(InitializerFunc initializer) {
        this.initializer = initializer;
        return this;
    }

    public GeneticBuilder setRegularizer(RegularizerFunc regularizer) {
        this.regularizer = regularizer;
        return this;
    }

    public GeneticBuilder setMutationRate(float mutationRate) {
        this.mutationRate = mutationRate;
        return this;
    }

    private int units;
    private InitializerFunc initializer;
    private RegularizerFunc regularizer;
    private float mutationRate;
}

