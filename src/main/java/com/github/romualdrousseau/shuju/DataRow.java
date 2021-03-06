package com.github.romualdrousseau.shuju;

import java.util.ArrayList;
import java.util.List;

import com.github.romualdrousseau.shuju.math.Tensor1D;

public class DataRow {
    public final static int FEATURES = 0;
    public final static int LABELS = 1;
    public final static int X = 0;
    public final static int y = 1;

    public List<Tensor1D> features() {
        return this.features;
    }

    public Tensor1D featuresAsOneVector() {
        if (this.features.size() == 1) {
            return this.features.get(0);
        } else {
            Tensor1D result = new Tensor1D(0);
            for (int i = 0; i < this.features.size(); i++) {
                result = result.concat(this.features.get(i));
            }
            return result;
        }
    }

    public DataRow addFeature(Tensor1D feature) {
        this.features.add(feature);
        return this;
    }

    public Tensor1D label() {
        return this.label;
    }

    public DataRow setLabel(Tensor1D label) {
        this.label = label;
        return this;
    }

    public boolean hasSameFeatures(DataRow other) {
        boolean result = this.features.size() == other.features.size();
        for (int i = 0; i < this.features.size() && result; i++) {
            result &= this.features.get(i).equals(other.features.get(i));
        }
        return result;
    }

    public boolean hasSameLabel(DataRow other) {
        return this.label.equals(other.label);
    }

    public boolean conflicts(DataRow other) {
        return this.hasSameFeatures(other) && !this.hasSameLabel(other);
    }

    public boolean equals(DataRow other) {
        return this.hasSameFeatures(other) && this.hasSameLabel(other);
    }

    public String toString() {
        String featuresString = "";
        boolean firstPass = true;
        for (Tensor1D feature : this.features) {
            if (firstPass) {
                featuresString = feature.toString();
                firstPass = false;
            } else {
                featuresString += ", " + feature.toString();
            }
        }

        String labelsString = label.toString();

        return String.format("[%s :- %s]", featuresString, labelsString);
    }

    private ArrayList<Tensor1D> features = new ArrayList<Tensor1D>();
    private Tensor1D label = null;
}
