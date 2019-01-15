package com.github.romualdrousseau.shuju.ml.ann;

import java.util.HashMap;

class BiasNode extends InputNode {
    public BiasNode() {
        super(-1);
    }

    public void generateCode() {
        System.out.println("x = 1;");
    }

    public double getOutput(double[] inputs, HashMap<Node, Double> cachedOutputs) {
        Double output = cachedOutputs.get(this);
        if (output == null) {
            output = (double) 1;
            cachedOutputs.put(this, output);
        }
        return (double) output;
    }
}
