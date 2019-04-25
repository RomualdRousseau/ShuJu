package com.github.romualdrousseau.shuju;

import com.github.romualdrousseau.shuju.json.JSONFactory;
import com.github.romualdrousseau.shuju.json.JSONArray;

public abstract class IFeature<T> implements StatisticClass {
    public static final int LABEL = -1;

    public IFeature() {
        this.value = null;
        this.probability = 0.0;
        this.emptyAsNoCost = false;
    }

    public IFeature(T value) {
        this.value = value;
        this.probability = 0.0;
        this.emptyAsNoCost = false;
    }

    public IFeature(T value, double probability) {
        this.value = value;
        this.probability = probability;
        this.emptyAsNoCost = false;
    }

    public T getValue() {
        return this.value;
    }

    public IFeature<T> setValue(T value) {
        this.value = value;
        return this;
    }

    public double getProbability() {
        return this.probability;
    }

    public IFeature<T> setProbability(double probability) {
        this.probability = probability;
        return this;
    }

    public boolean isEmptyAsNoCost() {
        return this.emptyAsNoCost;
    }

    public IFeature<T> setEmptyAsNoCost(boolean value) {
        this.emptyAsNoCost = value;
        return this;
    }

    public boolean isEmpty() {
        return this.value == null;
    }

    public boolean equals(IFeature<?> other) {
        return this.value.equals(other.value) && this.probability == other.probability;
    }

    public static boolean isNullOrEmpty(IFeature<?> feature) {
        return feature == null || feature.isEmpty();
    }

    public double costFunc(IFeature<?> predictedValue) {
        if (this.isEmptyAsNoCost() && this.isEmpty()) {
            return 0.0;
        }

        if (predictedValue.isEmptyAsNoCost() && predictedValue.isEmpty()) {
            return 0.0;
        }

        return this.costFuncImpl(predictedValue);
    }

    public String toString() {
        return String.format("[%s, %.2f]", this.value, this.probability);
    }

    public abstract float[] toVector();

    protected abstract JSONArray toJSON(JSONFactory jsonFactory);

    protected abstract void fromJSON(JSONArray json);

    protected abstract double costFuncImpl(IFeature<?> predictedValue);

    private T value;
    private double probability;
    private boolean emptyAsNoCost;
}
