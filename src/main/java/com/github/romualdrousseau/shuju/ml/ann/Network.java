package com.github.romualdrousseau.shuju.ml.ann;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Network {
    public void build(int numInputNodes, int numHiddenNodes, int numLayers, int numOutputNodes) {
        this.inputNodes = new InputNode[numInputNodes];
        for (int i = 0; i < numInputNodes; i++) {
            this.inputNodes[i] = new InputNode(i);
        }

        this.outputNodes = new OutputNode[numOutputNodes];
        for (int i = 0; i < numOutputNodes; i++) {
            this.outputNodes[i] = new OutputNode(i);
        }

        this.edges = new ArrayList<Edge>();
        if (numLayers == 0) {
            // connect the input layer to output layer
            for (int i = 0; i < numInputNodes; i++) {
                for (int j = 0; j < numOutputNodes; j++) {
                    this.edges.add(this.inputNodes[i].createEdge(this.outputNodes[j]));
                }
            }
        } else {
            Node[][] hiddenNodes = new HiddenNode[numLayers][numHiddenNodes];
            for (int j = 0; j < numLayers; j++) {
                for (int i = 0; i < numHiddenNodes; i++) {
                    hiddenNodes[j][i] = new HiddenNode();
                }
            }
            // connect input nodes to first hidden layer
            for (int j = 0; j < numInputNodes; j++) {
                for (int i = 0; i < numHiddenNodes; i++) {
                    this.edges.add(this.inputNodes[j].createEdge(hiddenNodes[0][i]));
                }
            }
            // connect hidden nodes together
            for (int k = 0; k < numLayers - 1; k++) {
                for (int j = 0; j < numHiddenNodes; j++) {
                    for (int i = 0; i < numHiddenNodes; i++) {
                        this.edges.add(hiddenNodes[k][j].createEdge(hiddenNodes[k + 1][i]));
                    }
                }
            }
            // connect the last hidden layer to output layer
            for (int i = 0; i < numHiddenNodes; i++) {
                for (int j = 0; j < numOutputNodes; j++) {
                    this.edges.add(hiddenNodes[numLayers - 1][i].createEdge(this.outputNodes[j]));
                }
            }
        }

        resetWeights();
    }

    public void resetWeights() {
        for (Edge e : edges) {
            e.reset();
        }
    }

    public void generateCode() {
        Node.sumLabel = 0;
        System.out.println("double x;");
        for (OutputNode node : this.outputNodes) {
            node.generateCode();
        }
    }

    public double[] evaluate(double[] inputs) {
        double[] result = new double[this.outputNodes.length];
        HashMap<Node, Double> cacheOutputs = new HashMap<Node, Double>();
        for (int i = 0; i < this.outputNodes.length; i++) {
            result[i] = this.outputNodes[i].getOutput(inputs, cacheOutputs);
        }
        return result;
    }

    public void train(double[] examples, double[] labels, double learnRate) {
        HashMap<Edge, Double> weights = new HashMap<Edge, Double>();

        weights = (new NetworkTrainer(this, examples, labels, learnRate)).call();
        for (Entry<Edge, Double> e : weights.entrySet()) {
            e.getKey().weight -= (double) e.getValue();
        }
    }

    public void train(double[][] examples, double[][] labels, int start, int end, double learnRate) {
        for (int i = 0; i < (end - start); i++) {
            int k = start + i;
            train(examples[k], labels[k], learnRate);
        }
    }

    public void train(double[][] examples, double[][] labels, int start, int end, int maxEpochs, double learnRate) {
        for (int n = 0; n < maxEpochs; n++) {
            train(examples, labels, start, end, learnRate);
        }
    }

    public void train(double[][] examples, double[][] labels, int start, int end, int maxEpochs, int batchSize,
            double learnRate) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        ArrayList<Future<HashMap<Edge, Double>>> future = new ArrayList<Future<HashMap<Edge, Double>>>(batchSize);
        ArrayList<HashMap<Edge, Double>> weights = new ArrayList<HashMap<Edge, Double>>(batchSize);

        for (int n = 0; n < maxEpochs; n++) {
            for (int i = 0; i < (end - start) / batchSize; i++) {
                // map
                for (int j = 0; j < batchSize; j++) {
                    int k = start + i * batchSize + j;
                    future.add(executor.submit(new NetworkTrainer(this, examples[k], labels[k], learnRate)));
                }
                // wait all threads and get results
                for (int j = 0; j < batchSize; j++) {
                    try {
                        weights.add(future.get(j).get());
                    } catch (Exception x) {
                    }
                }
                // reduce
                for (int j = 0; j < batchSize; j++) {
                    for (Entry<Edge, Double> e : weights.get(j).entrySet()) {
                        e.getKey().weight -= (double) e.getValue();
                    }
                }
            }
        }

        executor.shutdown();
    }

    public double computeAccurary(double[][] examples, double[][] labels, int start, int end) {
        double sum = 0;
        for (int i = start; i < end; i++) {
            double[] a = evaluate(examples[i]);
            for (int j = 0; j < a.length; j++) {
                double b = a[j] - labels[i][j];
                sum += 0.5 * b * b;
            }
        }
        return sum / (end - start);
    }

    protected InputNode[] inputNodes;
    protected OutputNode[] outputNodes;
    protected List<Edge> edges;
}
