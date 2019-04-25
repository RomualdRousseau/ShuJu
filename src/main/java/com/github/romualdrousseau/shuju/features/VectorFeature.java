package com.github.romualdrousseau.shuju.features;

import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONFactory;
import com.github.romualdrousseau.shuju.math.Vector;

public class VectorFeature extends IFeature<float[]> {
    public VectorFeature(float[] value) {
        super(value);
    }

    public VectorFeature(float[] value, double probability) {
        super(value, probability);
    }

    public boolean equals(IFeature<?> other) {
        return java.util.Arrays.equals(this.getValue(), (float[]) other.getValue()) && this.getProbability() == other.getProbability();
    }

    public float[] toVector() {
        return this.getValue();
    }

    protected JSONArray toJSON(JSONFactory jsonFactory) {
        JSONArray json = jsonFactory.newJSONArray();
        for(int i = 0; i < this.getValue().length; i++) {
            json.append(this.getValue()[i]);
        }
        return json;
    }

    protected void fromJSON(JSONArray json) {
        for(int i = 0; i < json.size(); i++) {
            this.getValue()[i] = json.getFloat(i);
        }
    }

    protected double costFuncImpl(IFeature<?> predictedValue) {
        return  Vector.scalar((float[]) predictedValue.getValue(),  this.getValue());
    }
}
