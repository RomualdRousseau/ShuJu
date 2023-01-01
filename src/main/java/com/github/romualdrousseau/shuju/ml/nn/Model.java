package com.github.romualdrousseau.shuju.ml.nn;

import java.util.LinkedList;
import java.util.function.Consumer;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;

public class Model {
    public Model() {
    }

    private Model(Model parent) {
        for(Layer layer : parent.layers) {
            this.add(layer.clone());
        }
    }

    public Model clone() {
        return new Model(this);
    }

    public Layer model(Tensor2D input) {
        return this.model(input, false);
    }

    public Layer model(Tensor2D input, boolean training) {
        for(Layer layer : this.layers) {
            layer.training = training;
            layer.output = layer.callForward(input);
            layer.lastInput = input;
            input = layer.output;
        }
        return this.layers.getLast();
    }

    public int getLastUnits() {
        return this.layers.getLast().units;
    }

    public int getLastChannels() {
        return this.layers.getLast().channels;
    }

    public void reset() {
        for(Layer layer : this.layers) {
            layer.reset();
        }
    }

    public Model add(LayerBuilder<?> builder) {
        if(builder.inputUnits == 0) {
            builder.setInputUnits(this.layers.getLast().units);
        }
        if(builder.inputChannels == 0) {
            builder.setInputChannels(this.layers.getLast().channels);
        }
        return add(builder.build());
    }

    public Model add(Layer layer) {
        assert (this.layers.size() == 0 || layer.inputUnits == this.layers.getLast().units);
        assert (this.layers.size() == 0 || layer.inputChannels == this.layers.getLast().channels);
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
