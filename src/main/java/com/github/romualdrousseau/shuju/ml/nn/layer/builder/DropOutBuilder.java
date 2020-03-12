package com.github.romualdrousseau.shuju.ml.nn.layer.builder;

import com.github.romualdrousseau.shuju.ml.nn.LayerBuilder;
import com.github.romualdrousseau.shuju.ml.nn.layer.DropOut;

public class DropOutBuilder extends LayerBuilder<DropOut> {

    public DropOutBuilder() {
        super();
    }

    public DropOut build() {
        return new DropOut();
    }
}

