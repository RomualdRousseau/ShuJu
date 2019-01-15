package com.github.romualdrousseau.shuju.ml.ann;

import java.util.HashMap;
import java.util.concurrent.Callable;

public class NetworkTrainer implements Callable<HashMap<Edge, Double>> {
    public NetworkTrainer(Network network, double[] examples, double[] labels, double rate) {
        this.network = network;
        this.examples = examples;
        this.labels = labels;
        this.rate = rate;
    }

    @Override
    public HashMap<Edge, Double> call() {
        HashMap<Node, Double> cacheOutputs = new HashMap<Node, Double>();
        for (OutputNode node : this.network.outputNodes) {
            node.getOutput(this.examples, cacheOutputs);
        }

        HashMap<Node, Double> cachedErrors = new HashMap<Node, Double>();
        for (Node node : this.network.inputNodes) {
            node.getError(this.labels, cacheOutputs, cachedErrors);
        }

        HashMap<Edge, Double> weights = new HashMap<Edge, Double>();
        for (Node node : this.network.inputNodes) {
            node.getWeights(this.rate, cacheOutputs, cachedErrors, weights);
        }

        return weights;
    }

    private Network network;
    private double[] examples;
    private double[] labels;
    private double rate;
}
