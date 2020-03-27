package com.github.romualdrousseau.shuju.ml.nn;

import com.github.romualdrousseau.shuju.json.JSONObject;

import com.github.romualdrousseau.shuju.math.Tensor3D;

public class Parameters3D {
    public Tensor3D W, G, M, V;

    public Parameters3D(int units) {
      this(1, 1, units);
    }

    public Parameters3D(int inputChannels, int inputUnits, int units) {
      this.W = new Tensor3D(inputChannels, units, inputUnits).zero();
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
      this.W = new Tensor3D(json);
      this.G = W.copy().zero();
      this.M = W.copy().zero();
      this.V = W.copy().zero();
    }

    public JSONObject toJSON() {
      return this.W.toJSON();
    }
  }
