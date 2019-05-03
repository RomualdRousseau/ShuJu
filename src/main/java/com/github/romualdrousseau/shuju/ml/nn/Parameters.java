package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.json.JSONObject;

import com.github.romualdrousseau.shuju.math.Matrix;

public class Parameters {
    public Matrix W, G, M, V;

    public Parameters(int units) {
      this(1, units);
    }

    public Parameters(int inputUnits, int units) {
      this.W = new Matrix(units, inputUnits, 0.0f);
      this.G = W.copy().zero();
      this.M = W.copy().zero();
      this.V = W.copy().zero();
    }

    public void reset() {
      this.W.zero();
      this.G.zero();
      this.M.zero();
      this.V.zero();
    }

    public void fromJSON(JSONObject json) {
      this.W = new Matrix(json);
      this.G = W.copy().zero();
      this.M = W.copy().zero();
      this.V = W.copy().zero();
    }

    public JSONObject toJSON() {
      return this.W.toJSON();
    }
  }
