package com.github.romualdrousseau.shuju.ml.nn;

import java.util.LinkedList;
import java.util.function.Consumer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.math.Matrix;

public class Model {
    public Model() {
    }

    public void reset() {
        for(Layer layer : this.layers) {
            layer.reset(false);
        }
    }

    public void setTrainingMode(boolean training) {
        for(Layer layer : this.layers) {
            layer.training = training;
        }
    }

    public Layer model(Vector input) {
        return this.model(new Matrix(input, false));
    }

    public Layer model(Matrix input) {
        for(Layer layer : this.layers) {
            layer.output = layer.callForward(input);
            layer.lastInput = input;
            input = layer.output;
        }
        return this.layers.getLast();
    }

    public Model add(Layer layer) {
        layer.model = this;
        this.layers.add(layer);
        return this;
    }

    public void visit(Consumer<Layer> visitFunc) {
        this.layers.iterator().forEachRemaining(visitFunc);
    }

    public void visitBackward(Consumer<Layer> visitFunc) {
        this.layers.descendingIterator().forEachRemaining(visitFunc);
    }

    public void fromJSON(JSONArray json) {
        if (json.size() != this.layers.size()) {
            throw new IllegalArgumentException("model must match the model layout.");
        }
        int i = 0;
        for(Layer layer : this.layers) {
            layer.fromJSON(json.getJSONObject(i++));
        }
    }

    public JSONArray toJSON() {
        JSONArray json = JSON.newJSONArray();
        for(Layer layer : this.layers) {
            json.append(layer.toJSON());
        }
        return json;
    }

    private LinkedList<Layer> layers = new LinkedList<Layer>();
}
