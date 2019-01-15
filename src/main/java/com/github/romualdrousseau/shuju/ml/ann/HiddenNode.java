package com.github.romualdrousseau.shuju.ml.ann;

import java.util.ArrayList;

class HiddenNode extends Node {
    public HiddenNode() {
        this.incomingEdges = new ArrayList<Edge>();
        this.outcomingEdges = new ArrayList<Edge>();
        addBias();
    }
}
