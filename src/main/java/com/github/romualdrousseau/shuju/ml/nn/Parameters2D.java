package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.json.JSONObject;
import com.github.romualdrousseau.shuju.math.deprecated.Tensor2D;

public class Parameters2D {
    public Tensor2D W, G, M, V;

    public Parameters2D(int units) {
        this(1, units);
    }

    public Parameters2D(int inputUnits, int units) {
        this.W = new Tensor2D(units, inputUnits).zero();
        this.G = W.copy().zero();
        this.M = W.copy().zero();
        this.V = W.copy().zero();
    }

    private Parameters2D(Parameters2D parent) {
        this.W = parent.W.copy();
        this.G = W.copy().zero();
        this.M = W.copy().zero();
        this.V = W.copy().zero();
    }

    public Parameters2D clone() {
        return new Parameters2D(this);
    }

    public void reset() {
        this.W.zero();
        this.G.zero();
        this.M.zero();
        this.V.zero();
    }

    public void fromJSON(JSONObject json) {
        this.W = new Tensor2D(json);
        this.G = W.copy().zero();
        this.M = W.copy().zero();
        this.V = W.copy().zero();
    }

    public JSONObject toJSON() {
        return this.W.toJSON();
    }
}
