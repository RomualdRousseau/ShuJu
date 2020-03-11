package com.github.romualdrousseau.shuju.ml.qlearner;

import com.github.romualdrousseau.shuju.math.*;
import com.github.romualdrousseau.shuju.ml.nn.*;
import com.github.romualdrousseau.shuju.ml.nn.loss.*;
import com.github.romualdrousseau.shuju.ml.nn.optimizer.builder.*;
import com.github.romualdrousseau.shuju.ml.nn.activation.*;
import com.github.romualdrousseau.shuju.ml.nn.layer.builder.DenseBuilder;
import com.github.romualdrousseau.shuju.ml.nn.layer.builder.ActivationBuilder;

public class QMatrixNnImpl extends QMatrix {
    public QMatrixNnImpl(QEnvironment env) {
        this(env, 1, 1, -1);
    }

    public QMatrixNnImpl(QEnvironment env, double hiddenRatio, int hiddenLayer, int memorySpace) {
        this.numStates = env.numStates;
        this.numActions = env.numActions;

        double binaryRatio = 1.0 / Math.log(2);
        this.numInputs = (int) Math.ceil(Math.log(numStates) * binaryRatio);
        int numHiddens = (int) Math.ceil(numInputs * hiddenRatio);
        this.numOutputs = (int) Math.ceil(Math.log(numActions) * binaryRatio);

        this.model = new Model()
            .add(new DenseBuilder()
                .setInputUnits(numInputs)
                .setUnits(numHiddens)
                .build())
            .add(new ActivationBuilder()
                .setActivation(new Relu())
                .build())
            .add(new DenseBuilder()
                .setInputUnits(numHiddens)
                .setUnits(numOutputs + 1)
                .build())
            .add(new ActivationBuilder()
                .setActivation(new Tanh())
                .build());

        this.optimizer = new OptimizerRMSPropBuilder().build(this.model);

        this.loss = new Loss(new Huber());

        this.memoryMap = new MemoryMap(memorySpace);
    }

    public void reset() {
        this.memoryMap.clear();
        this.model.reset();
    }

    public void train(int s, int a, double v, double learnRate) {
        this.memoryMap.put(s, a, v, 1.0);

        this.optimizer.zeroGradients();

        for (MemoryCell memory : this.memoryMap.replay(0, this.numStates)) {
            Vector state = new Vector(MemoryCell.IntToFloatArray(memory.state, numInputs));
            Vector action = new Vector(MemoryCell.IntToFloatArray(memory.action, numOutputs + 1));
            action.set(numOutputs, (float) memory.reward);
            this.optimizer.minimize(this.loss.loss(this.model.model(state), action));
        }

        this.optimizer.step();
    }

    public int predictAction(int s) {
        Vector a = new Vector(MemoryCell.IntToFloatArray(s, numInputs));
        Vector b = this.model.model(a).detachAsVector();
        return MemoryCell.FloatArrayToInt(b.getFloats(), numOutputs);
    }

    public double predictReward(int s) {
        Vector a = new Vector(MemoryCell.IntToFloatArray(s, numInputs));
        Vector b = this.model.model(a).detachAsVector();
        return b.get(numOutputs);
    }

    private int numStates;
    private int numActions;
    private int numInputs;
    private int numOutputs;
    private Model model;
    private Optimizer optimizer;
    private Loss loss;
    private MemoryMap memoryMap;
}
