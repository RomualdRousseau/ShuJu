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
                        .setUnits(numHiddens))
                .add(new ActivationBuilder()
                        .setActivation(new Relu()))
                .add(new DenseBuilder()
                        .setUnits(numOutputs + 1))
                .add(new ActivationBuilder()
                        .setActivation(new Tanh()));

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
            Tensor2D state = new Tensor2D(MemoryCell.IntToFloatArray(memory.state, numInputs), true);
            Tensor2D action = new Tensor2D(MemoryCell.IntToFloatArray(memory.action, numOutputs + 1), true);
            action.set(numOutputs, 0, (float) memory.reward);
            this.optimizer.minimize(this.loss.loss(this.model.model(state), action));
        }

        this.optimizer.step();
    }

    public int predictAction(int s) {
        Tensor2D a = new Tensor2D(MemoryCell.IntToFloatArray(s, numInputs), true);
        Tensor2D b = this.model.model(a).detach();
        return MemoryCell.FloatArrayToInt(b.transpose().getFloats(0), numOutputs);
    }

    public double predictReward(int s) {
        Tensor2D a = new Tensor2D(MemoryCell.IntToFloatArray(s, numInputs), true);
        Tensor2D b = this.model.model(a).detach();
        return b.get(numOutputs, 0);
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
