package com.github.romualdrousseau.shuju.features;

import com.github.romualdrousseau.shuju.IFeature;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONFactory;

public class StringFeature extends IFeature<String> {
    public StringFeature(String value) {
        super(value);
    }

    public StringFeature(String value, double probability) {
        super(value, probability);
    }

    public float[] toVector() {
        return null;
    }

    protected JSONArray toJSON(JSONFactory jsonFactory) {
        JSONArray json = jsonFactory.newJSONArray();
        json.append(this.getValue());
        return json;
    }

    protected void fromJSON(JSONArray json) {
        this.setValue(json.getString(0));
    }

    protected double costFuncImpl(IFeature<?> predictedValue) {
        return predictedValue.getValue().equals(this.getValue()) ? 0.0 : 1.0;
    }
}
