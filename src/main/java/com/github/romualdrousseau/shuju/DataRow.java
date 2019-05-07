package com.github.romualdrousseau.shuju;

import java.util.ArrayList;
import java.util.List;

import com.github.romualdrousseau.shuju.math.Vector;

public class DataRow {
    public final static int FEATURES = 0;
    public final static int LABELS = 1;

    public List<Vector> features() {
        return this.features;
    }

    public Vector featuresAsOneVector() {
        Vector result = new Vector(0);
        for(int i = 0; i < this.features.size(); i++) {
            result = result.concat(this.features.get(i));
        }
        return result;
    }

    public DataRow addFeature(Vector feature) {
        this.features.add(feature);
        return this;
    }

    public Vector label() {
        return this.label;
    }

    public DataRow setLabel(Vector label) {
        this.label = label;
        return this;
    }

    public boolean hasSameFeatures(DataRow other) {
        boolean result = this.features.size() == other.features.size();
        for(int i = 0; i < this.features.size() && result; i++) {
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
        for (Vector feature : this.features) {
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

    private ArrayList<Vector> features = new ArrayList<Vector>();
    private Vector label = null;
}
