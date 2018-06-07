package org.shuju.ml.ann;

import java.util.List;
import java.util.HashMap;

abstract class Node
{
  public static BiasNode BIAS = null;
  public static int sumLabel = 0;

  public static double activation(double x) {
    return 1.0 / (1.0 + Math.exp(-x));
    //return x;
  }
    
  public static double activation_derivative(double x) {
    return x * (1.0 - x);
    //return 1;
  }
  
  public Edge createEdge(Node node) {
    Edge edge = new Edge(this, node);
    edge.source.outcomingEdges.add(edge);
    edge.target.incomingEdges.add(edge); 
    return edge;
  }
  
  public void addBias() {
    if(BIAS == null) {
      BIAS = new BiasNode();
    }
    BIAS.createEdge(this);
  }
  
  public void generateCode() {
    assert(this.incomingEdges != null);

    String variabeName = "s" + (sumLabel++);
    System.out.println("double " + variabeName + " = 0;");
    for(Edge e: incomingEdges) {
      e.source.generateCode();
      System.out.println(variabeName + " += " + e.weight + " * x;");
    }
    System.out.println("x = f(" + variabeName + ");");
  }

  public double getOutput(double[] inputs, HashMap<Node, Double> cachedOutputs) {
    assert(this.incomingEdges != null);   
    Double output = cachedOutputs.get(this);
    if(output == null) {
      double sum = 0;
      for(Edge e: incomingEdges) {
        sum += e.weight * e.source.getOutput(inputs, cachedOutputs);
      }
      output = activation(sum);
      cachedOutputs.put(this, output);
    }
    return (double) output;
  }
  
  public double getError(double[] labels, HashMap<Node, Double> cachedOutputs, HashMap<Node, Double> cachedErrors) {
    assert(this.outcomingEdges != null); 
    Double error = cachedErrors.get(this);
    if(error == null) {
      double sum = 0;
      for(Edge e: outcomingEdges) {
        sum += e.weight * e.target.getError(labels, cachedOutputs, cachedErrors);
      }
      Double output = cachedOutputs.get(this);
      assert(output != null);
      error = sum * activation_derivative(output);
      cachedErrors.put(this, error);
    }
    return (double) error;
  }

  public void getWeights(double rate, HashMap<Node, Double> cachedOutputs, HashMap<Node, Double> cachedErrors, HashMap<Edge, Double> weights) {
    assert(this.outcomingEdges != null); 
    updateWeights(rate, cachedOutputs, cachedErrors, weights);
    for(Edge e: outcomingEdges) {
      e.target.getWeights(rate, cachedOutputs, cachedErrors, weights);
    }
  }
  
  protected void updateWeights(double rate, HashMap<Node, Double> cachedOutputs, HashMap<Node, Double> cachedErrors, HashMap<Edge, Double> weights) {
    assert(this.incomingEdges != null);
    Double error = cachedErrors.get(this);
    assert(error != null);
    for(Edge e: this.incomingEdges) {
      Double input = cachedOutputs.get(e.source);
      assert(input != null);  
      Double weight = weights.get(e);
      if(weight == null) {
        weight = (double) 0;
      }
      weight = rate * error * input; // TODO: PID ?
      weights.put(e, weight);
    }   
  }

  protected List<Edge> incomingEdges;
  protected List<Edge> outcomingEdges;
}
