package com.github.romualdrousseau.shuju.ml.ann;

class Edge {
    public Node source;
    public Node target;
    public double weight;

    public Edge(Node source, Node target) {
        this.source = source;
        this.target = target;
    }

    public void reset() {
        this.weight = 1 - 2 * Math.random();
    }
}
