package org.shuju.ann;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class NetworkTrainer implements Callable<HashMap<Edge, Double>>
{
  public NetworkTrainer(Network network, double[] examples, double[] labels, double rate) {
    this.m_network = network;
    this.m_examples = examples;
    this.m_labels = labels;
    this.m_rate = rate;
  }
  
  @Override
  public HashMap<Edge, Double> call() {
    HashMap<Node, Double> cacheOutputs = new HashMap<Node, Double>();
    for(OutputNode node: this.m_network.m_outputNodes) {
      node.getOutput(this.m_examples, cacheOutputs);
    }

    HashMap<Node, Double> cachedErrors = new HashMap<Node, Double>();
    for(Node node: this.m_network.m_inputNodes) {
      node.getError(this.m_labels, cacheOutputs, cachedErrors);
    }
    
    HashMap<Edge, Double> weights = new HashMap<Edge, Double>();
    for(Node node: this.m_network.m_inputNodes) {
      node.getWeights(this.m_rate, cacheOutputs, cachedErrors, weights);
    }
    
    return weights;
  }
  
  private Network m_network;
  private double[] m_examples;
  private double[] m_labels;
  private double m_rate;
}
