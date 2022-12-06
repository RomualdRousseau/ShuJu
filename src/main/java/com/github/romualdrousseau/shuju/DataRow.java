package com.github.romualdrousseau.shuju;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.Tensor1D;

public class DataRow {
    public final static int FEATURES = 0;
    public final static int LABELS = 1;
    public final static int X = 0;
    public final static int y = 1;

    public DataRow() {
    }

    public DataRow(final JSONArray jsonFeatures, final JSONObject jsonTarget) {
        IntStream.range(0, jsonFeatures.size())
                .mapToObj(this.features::get)
                .forEach(this::addFeature);
        this.setLabel(new Tensor1D(jsonTarget));
    }

    public List<Tensor1D> features() {
        return this.features;
    }

    public Tensor1D featuresAsOneVector() {
        if (this.features.size() == 1) {
            return this.features.get(0);
        } else {
            return IntStream.range(0, this.features.size())
                    .mapToObj(this.features::get)
                    .reduce((r, x) -> r.concat(x)).get();
        }
    }

    public DataRow addFeature(final Tensor1D feature) {
        this.features.add(feature);
        return this;
    }

    public Tensor1D label() {
        return this.label;
    }

    public DataRow setLabel(final Tensor1D label) {
        this.label = label;
        return this;
    }

    public boolean hasSameFeatures(final DataRow other) {
        boolean valid = this.features.size() == other.features.size();
        return valid && IntStream.range(0, this.features.size())
                .allMatch(i -> this.features.get(i).equals(other.features.get(i)));
    }

    public boolean hasSameLabel(final DataRow other) {
        return this.label.equals(other.label);
    }

    public boolean conflicts(final DataRow other) {
        return this.hasSameFeatures(other) && !this.hasSameLabel(other);
    }

    public boolean equals(final DataRow other) {
        return this.hasSameFeatures(other) && this.hasSameLabel(other);
    }

    public String toString() {
        final String featuresString = this.features.stream().map(x -> x.toString())
                .reduce((result, x) -> result + ", " + x).orElse("");
        final String labelsString = label.toString();
        return String.format("[%s :- %s]", featuresString, labelsString);
    }

    private final ArrayList<Tensor1D> features = new ArrayList<Tensor1D>();
    private Tensor1D label = null;
}
