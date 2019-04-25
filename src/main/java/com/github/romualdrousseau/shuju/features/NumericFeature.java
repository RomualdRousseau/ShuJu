package com.github.romualdrousseau.shuju.features;

import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONFactory;

public class NumericFeature extends IFeature<Float> {
    public NumericFeature(Float value) {
        super(value);
    }

    public NumericFeature(Float value, double probability) {
        super(value, probability);
    }

    public float[] toVector() {
        float[] result = new float[1];
        result[0] = this.getValue();
        return result;
    }

    protected JSONArray toJSON(JSONFactory jsonFactory) {
        JSONArray json = jsonFactory.newJSONArray();
        json.append(this.getValue());
        return json;
    }

    protected void fromJSON(JSONArray json) {
        this.setValue(json.getFloat(0));
    }

    protected double costFuncImpl(IFeature<?> predictedValue) {
        float dist = (Float) predictedValue.getValue() - this.getValue();
        return dist * dist;
    }
}
