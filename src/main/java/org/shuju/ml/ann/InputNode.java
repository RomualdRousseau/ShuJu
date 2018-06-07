package org.shuju.ml.ann;

import java.util.HashMap;
import java.util.ArrayList;

class InputNode extends Node
{
  public InputNode(int index) {
    this.incomingEdges = null;
    this.outcomingEdges = new ArrayList<Edge>();
    this.m_index = index;
  }
  
  public void generateCode() {
    System.out.println("x = inputs[" + this.m_index + "];");
  }
  
  public double getOutput(double[] inputs, HashMap<Node, Double> cachedOutputs) {
    Double output = cachedOutputs.get(this);
    if(output == null) {
      output = inputs[this.m_index];
      cachedOutputs.put(this, output);
    }
    return (double) output;
  }
  
  public double getError(double[] labels, HashMap<Node, Double> cachedOutputs, HashMap<Node, Double> cachedErrors) {
    assert(this.outcomingEdges != null);
    for(Edge e: outcomingEdges) {
      e.target.getError(labels, cachedOutputs, cachedErrors);
    }
    return (double) 0;
  }

  public void getWeights(double rate, HashMap<Node, Double> cachedOutputs, HashMap<Node, Double> cachedErrors, HashMap<Edge, Double> weights) {
    assert(this.outcomingEdges != null);
    for(Edge e: outcomingEdges) {
        e.target.getWeights(rate, cachedOutputs, cachedErrors, weights);
    }
  }

  private int m_index;
}
