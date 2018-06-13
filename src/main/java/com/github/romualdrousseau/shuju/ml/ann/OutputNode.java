package com.github.romualdrousseau.shuju.ml.ann;

import java.util.HashMap;
import java.util.ArrayList;

class OutputNode extends Node
{
  public OutputNode(int index) {
    this.incomingEdges = new ArrayList<Edge>();
    this.outcomingEdges = null;
    this.index = index;
    addBias(); //<>//
  }
  
  public double getError(double[] labels, HashMap<Node, Double> cachedOutputs, HashMap<Node, Double> cachedErrors) {
    Double error = cachedErrors.get(this);
    if(error == null) {
      Double output = cachedOutputs.get(this);
      assert(output != null);
      error = -(labels[this.index] - output) * activation_derivative(output);
      cachedErrors.put(this, error);
    }
    return (double) error;
  }
  
  public void getWeights(double rate, HashMap<Node, Double> cachedOutputs, HashMap<Node, Double> cachedErrors, HashMap<Edge, Double> weights) {
    updateWeights(rate, cachedOutputs, cachedErrors, weights);
  }
  
  private int index;
}
