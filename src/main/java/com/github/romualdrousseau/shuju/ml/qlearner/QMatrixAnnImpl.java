package com.github.romualdrousseau.shuju.ml.qlearner;

import java.util.HashMap;
import java.util.Map.Entry;

import com.github.romualdrousseau.shuju.ml.ann.*;

public class QMatrixAnnImpl extends QMatrix
{
  public QMatrixAnnImpl(QEnvironment env) {
    this(env, 1, 1, -1);
  }

  public QMatrixAnnImpl(QEnvironment env, double hiddenRatio, int hiddenLayer, int memorySpace) {
    this.numStates = env.numStates;
    this.numActions = env.numActions;

    double binaryRatio = 1.0 / Math.log(2);
    this.numInputs = (int) Math.ceil(Math.log(numStates) * binaryRatio);
    int numHiddens = (int) Math.ceil(numInputs * hiddenRatio);
    this.numOutputs = (int) Math.ceil(Math.log(numActions) * binaryRatio);

    this.network = new Network();
    this.network.build(numInputs, numHiddens, hiddenLayer, numOutputs + 1);

    this.memoryMap =  new MemoryMap(memorySpace);
  }
  
  public void reset() {
    this.memoryMap.clear();
    this.network.resetWeights();
  }
  
  public void train(int s, int a, double v, double learnRate) {
    this.memoryMap.put(s, a, v, 1.0);

    for(MemoryCell memory: this.memoryMap.replay(0, this.numStates)) {
      double[] state = MemoryCell.IntToDoubleArray(memory.state, numInputs);
      double[] action =  MemoryCell.IntToDoubleArray(memory.action, numOutputs + 1);
      action[numOutputs] = memory.reward;
      this.network.train(state, action, learnRate);
    }
  }
  
  public int predictAction(int s) {
    double[] a = MemoryCell.IntToDoubleArray(s, numInputs);
    double[] b = this.network.evaluate(a);
    return MemoryCell.DoubleArrayToInt(b, numOutputs);
  }
  
  public double predictReward(int s) {
    double[] a = MemoryCell.IntToDoubleArray(s, numInputs);
    double[] b= this.network.evaluate(a);
    return b[numOutputs];
  }

  private int numStates;
  private int numActions;
  private int numInputs;
  private int numOutputs;
  private Network network;
  private MemoryMap memoryMap;
}
