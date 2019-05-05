package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.json.JSON;
import com.github.romualdrousseau.shuju.json.JSONArray;

import com.github.romualdrousseau.shuju.math.Scalar;
import com.github.romualdrousseau.shuju.math.Vector;
import com.github.romualdrousseau.shuju.math.Matrix;

public class Model {
    protected Layer start;
    protected Layer end;

    public Model() {
        this.start = new LayerBuilder().build();
        this.end = this.start;
    }

    public void reset() {
        for (Layer layer = this.start.next; layer != null; layer = layer.next) {
            layer.reset();
        }
    }

    public Model add(Layer layer) {
        layer.prev = this.end;
        this.end.next = layer;
        this.end = layer;
        return this;
    }

    public Layer model(Vector input) {
        return this.model(new Matrix(input));
    }

    public Layer model(Matrix input) {
        this.start.output = input;
        for (Layer layer = this.start.next; layer != null; layer = layer.next) {
            Matrix net = Scalar.xw_plus_b(layer.prev.output, layer.weights.W, layer.biases.W);
            layer.output = layer.activation.apply(net);
        }
        return this.end;
    }

    public void fromJSON(JSONArray json) {
        int i = json.size();
        for (Layer layer = this.start.next; layer != null; layer = layer.next, i--)
            ;
        if (i != 0) {
            throw new IllegalArgumentException("model must match the model layout.");
        }
        for (Layer layer = this.start.next; layer != null; layer = layer.next, i++) {
            layer.fromJSON(json.getJSONObject(i));
        }
    }

    public JSONArray toJSON() {
        JSONArray json = JSON.newJSONArray();
        for (Layer layer = this.start.next; layer != null; layer = layer.next) {
            json.append(layer.toJSON());
        }
        return json;
    }
}
