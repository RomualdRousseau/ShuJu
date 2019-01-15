package com.github.romualdrousseau.shuju;

import java.util.List;
import java.util.ArrayList;

public class Result implements StatisticClass {
    public Result(DataRow features, IFeature<?> label, double probability) {
        this.features = features;
        if (label != null) {
            this.labels.add(label);
        }
        this.probability = probability;
    }

    public DataRow features() {
        return this.features;
    }

    public Result setFeatures(DataRow features) {
        this.features = features;
        return this;
    }

    public List<IFeature<?>> labels() {
        return this.labels;
    }

    public IFeature<?> getLabel() {
        if (this.labels.size() == 0) {
            return null;
        } else {
            return this.labels.get(0);
        }
    }

    public Result setLabel(IFeature<?> label) {
        this.labels.clear();
        if (label != null) {
            this.labels.add(label);
        }
        return this;
    }

    public Result addLabel(IFeature<?> label) {
        this.labels.add(label);
        return this;
    }

    public double getProbability() {
        return this.probability;
    }

    public Result setProbability(double probability) {
        this.probability = probability;
        return this;
    }

    public boolean isUndefined() {
        return this.labels.size() == 0;
    }

    public double getError() {
        return (1.0 - this.probability) * (1.0 - this.probability);
    }

    public String toString() {
        String featuresString = (this.features == null) ? "<null>" : this.features.toString();

        String labelString = "";
        if (this.labels.size() == 0) {
            labelString = "<undefined>";
        } else {
            boolean firstPass = true;
            for (IFeature<?> label : this.labels) {
                if (firstPass) {
                    labelString = label.toString();
                    firstPass = false;
                } else {
                    labelString += ", " + label.toString();
                }
            }
        }

        return String.format("[%s :- %s, %.2f]", featuresString, labelString, this.probability);
    }

    public int hashCode() {
        String result = "";
        if (this.labels.size() == 0) {
            result = "undefined";
        } else {
            boolean firstPass = true;
            for (IFeature<?> label : this.labels) {
                if (firstPass) {
                    result = label.getValue().toString();
                    firstPass = false;
                } else {
                    result += "|" + label.getValue().toString();
                }
            }
        }
        return result.hashCode();
    }

    private DataRow features;
    private List<IFeature<?>> labels = new ArrayList<IFeature<?>>();
    private double probability;
}
