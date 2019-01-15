package com.github.romualdrousseau.shuju.features;

import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.util.FuzzyString;

public class FuzzyFeature extends StringFeature {
    public FuzzyFeature(String value) {
        super(value);
    }

    public FuzzyFeature(String value, double probability) {
        super(value, probability);
    }

    public FuzzyFeature setTokenizer(boolean tokenize, String tokenSeparator) {
        this.tokenize = tokenize;
        this.tokenSeparator = tokenSeparator;
        return this;
    }

    protected double costFuncImpl(IFeature<?> predictedValue) {
        double dist = 1.0;
        if (this.tokenize) {
            dist = FuzzyString.distance(String.valueOf(predictedValue.getValue()), this.getValue(),
                    this.tokenSeparator);
        } else {
            dist = FuzzyString.distance(String.valueOf(predictedValue.getValue()).replaceAll(this.tokenSeparator, ""),
                    this.getValue());
        }
        return dist * dist;
    }

    private boolean tokenize = false;
    private String tokenSeparator = " ";
}
